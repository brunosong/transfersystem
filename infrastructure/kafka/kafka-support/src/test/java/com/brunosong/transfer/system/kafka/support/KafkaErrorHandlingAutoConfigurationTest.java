package com.brunosong.transfer.system.kafka.support;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 브로커 없이 배선만 확인한다.
 *
 * 확인하려는 것 셋이다. 부트 자동 설정만으로 KafkaTemplate 이 뜨는지, 그것을
 * KafkaTemplate&lt;String, Object&gt; 로 주입받을 수 있는지(발행 어댑터가 그렇게 받는다),
 * 그리고 서비스가 자기 에러 핸들러를 두면 공유 것이 물러나는지다.
 */
class KafkaErrorHandlingAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    KafkaAutoConfiguration.class,
                    KafkaErrorHandlingAutoConfiguration.class))
            .withPropertyValues("spring.kafka.bootstrap-servers=localhost:9092");

    @Test
    void 부트가_템플릿을_만들고_공유_에러핸들러가_붙는다() {
        runner.run(context -> {
            assertThat(context).hasSingleBean(CommonErrorHandler.class);
            assertThat(context.getBean(CommonErrorHandler.class)).isInstanceOf(DefaultErrorHandler.class);
            // 발행 어댑터가 이 타입으로 주입받는다. 여기서 안 되면 기동할 때 터진다
            assertThat(context.getBean(KafkaTemplate.class)).isNotNull();
        });
    }

    @Test
    void 서비스가_자기_핸들러를_두면_공유_것은_물러난다() {
        runner.withUserConfiguration(ServiceOwnErrorHandler.class).run(context -> {
            assertThat(context).hasSingleBean(CommonErrorHandler.class);
            assertThat(context.getBean(CommonErrorHandler.class))
                    .isSameAs(context.getBean("serviceErrorHandler"));
        });
    }

    @Configuration
    static class ServiceOwnErrorHandler {
        @Bean
        CommonErrorHandler serviceErrorHandler() {
            return new DefaultErrorHandler(new FixedBackOff(0L, 0L));
        }
    }
}
