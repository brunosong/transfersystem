package com.brunosong.transfer.system.kafka.support;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.util.backoff.ExponentialBackOff;

/**
 * 실패한 레코드를 어떻게 할지에 대한 조직 공통 정책.
 *
 * 카프카 배선 자체는 스프링 부트 자동 설정이 spring.kafka.* 를 읽어 다 한다.
 * ConsumerFactory, ProducerFactory, KafkaTemplate, 리스너 컨테이너 팩토리가 그렇다.
 * 이 모듈은 그 위에 얹는 것 하나만 갖는다.
 *
 * 부트는 CommonErrorHandler 빈이 있으면 컨테이너 팩토리에 알아서 붙인다. 그래서
 * 이 빈 하나만 클래스패스에 있으면 두 서비스가 같은 실패 정책을 쓴다.
 *
 * ConditionalOnMissingBean 이 핵심이다. 서비스가 자기 CommonErrorHandler 를 정의하면
 * 이쪽은 아예 만들어지지 않는다. 전에는 공유 설정과 서비스 설정이 둘 다 존재하는데
 * 리스너마다 다른 것이 붙어, 한쪽 리스너만 DLT 가 없는 상태가 되어 있었다.
 * 조건을 걸면 그런 상태 자체가 생길 수 없다. 둘 중 하나만 남는다.
 */
@Slf4j
@AutoConfiguration(before = KafkaAutoConfiguration.class)
@ConditionalOnClass(KafkaOperations.class)
public class KafkaErrorHandlingAutoConfiguration {

    /** 실패한 레코드를 보낼 토픽 접미사. 원본 토픽 이름 뒤에 붙는다 */
    private static final String DLT_SUFFIX = ".DLT";

    /** 재시도 시작 간격 */
    private static final long INITIAL_INTERVAL_MS = 1_000L;

    /** 재시도를 그만두는 시점 */
    private static final long MAX_ELAPSED_MS = 30_000L;

    @Bean
    @ConditionalOnMissingBean(CommonErrorHandler.class)
    public CommonErrorHandler kafkaErrorHandler(KafkaOperations<?, ?> kafkaOperations) {
        // 간격 없이 반복하면 상대가 잠깐 죽은 경우에도 재시도가 순식간에 소진된다
        ExponentialBackOff backOff = new ExponentialBackOff(INITIAL_INTERVAL_MS, 2.0);
        backOff.setMaxElapsedTime(MAX_ELAPSED_MS);

        // 파티션을 -1 로 두어 DLT 토픽의 파티션 수가 원본과 달라도 보낼 수 있게 한다.
        // 기본값은 원본과 같은 파티션 번호라, DLT 파티션이 더 적으면 발행 자체가 실패한다
        ConsumerRecordRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaOperations,
                (record, exception) -> new TopicPartition(record.topic() + DLT_SUFFIX, -1));

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);

        // 역직렬화 실패는 몇 번을 다시 해도 결과가 같다. 재시도하지 않고 바로 DLT 로 보낸다
        errorHandler.addNotRetryableExceptions(
                DeserializationException.class,
                MessageConversionException.class,
                ClassCastException.class);

        log.info("카프카 실패 레코드를 <원본토픽>{} 으로 보냅니다", DLT_SUFFIX);
        return errorHandler;
    }
}
