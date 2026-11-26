package com.brunosong.deployment.system.targetagent.service.messaging.listener.kafka;

import com.brunosong.deployment.contract.ApplyChunkCommand;
import com.brunosong.deployment.system.targetagent.service.dto.message.ApplyChunkRequest;
import com.brunosong.deployment.system.targetagent.service.messaging.mapper.TargetAgentMessagingDataMapper;
import com.brunosong.deployment.system.targetagent.service.ports.input.message.listener.ApplyChunkCommandListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.BatchListenerFailedException;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class ApplyChunkCommandKafkaListener {

    private final ApplyChunkCommandListener applyChunkCommandListener;
    private final TargetAgentMessagingDataMapper targetAgentMessagingDataMapper;
    /** 지금 처리 중인 명령 수. 컨슈머 스레드끼리 함께 세므로 AtomicInteger 를 쓴다 */
    private final AtomicInteger inFlight = new AtomicInteger();

    public ApplyChunkCommandKafkaListener(ApplyChunkCommandListener applyChunkCommandListener,
                                             TargetAgentMessagingDataMapper targetAgentMessagingDataMapper) {
        this.applyChunkCommandListener = applyChunkCommandListener;
        this.targetAgentMessagingDataMapper = targetAgentMessagingDataMapper;
    }

    // 컨테이너 팩토리를 지정하지 않는다. 부트가 만든 기본 팩토리를 쓰고, 거기에
    // kafka-support 의 공통 에러 핸들러가 붙는다. 전에는 서비스가 자기 팩토리를
    // 따로 만들면서 그 정책이 안 붙어 이 리스너만 DLT 가 없었다
    @KafkaListener(topics = "${target-agent.data-migration-request-topic-name}")
    public void receive(@Payload List<ApplyChunkCommand> message,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {

        // forEach 로 돌면 한 건이 실패했을 때 배치 전체가 다시 처리된다.
        // 앞의 건은 중복 처리되고, 재시도를 소진하면 뒤의 건은 통째로 건너뛰어진다.
        // 실패한 위치를 BatchListenerFailedException 에 담아 던지면 그 앞은 커밋되고
        // 이 건부터 다시 시도된다
        for (int index = 0; index < message.size(); index++) {
            ApplyChunkCommand command = message.get(index);
            // 대상 DB에 동시에 몇 건이 들어가는지 보려고 시작과 끝을 남긴다(ADR 0010 실험).
            // 스레드 이름은 로그 패턴에 이미 찍힌다
            int running = inFlight.incrementAndGet();
            long startedAt = System.currentTimeMillis();
            log.info("반영 시작. 동시={}, partition={}, offset={}, key={}, chunkNo={}",
                    running, partitions.get(index), offsets.get(index), keys.get(index), command.getChunkNo());
            try {
                ApplyChunkRequest request = targetAgentMessagingDataMapper.toApplyChunkRequest(command);

                applyChunkCommandListener.apply(request);
            } catch (RuntimeException e) {
                throw new BatchListenerFailedException(
                        "반영 명령 처리에 실패했습니다. deploymentId=" + command.getDeploymentId(), e, index);
            } finally {
                log.info("반영 끝. 동시={}, partition={}, offset={}, {}ms",
                        inFlight.decrementAndGet(), partitions.get(index), offsets.get(index),
                        System.currentTimeMillis() - startedAt);
            }
        }
    }

}
