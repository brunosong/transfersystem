package com.brunosong.deployment.system.datamigration.service.helper;

import com.brunosong.deployment.system.datamigration.service.domain.valueobject.AppliedContent;
import com.brunosong.deployment.system.datamigration.service.dto.message.ApplyChunkRequest;
import com.brunosong.deployment.system.datamigration.service.dto.message.ApplyResultMessage;
import com.brunosong.deployment.system.datamigration.service.ports.output.message.publisher.deployment.DataMigrationResponsePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageResponseHelper {

    private final DataMigrationResponsePublisher dataMigrationResponsePublisher;

    public void failsResponseMessage(ApplyChunkRequest request, Exception e) {
        log.error("반영 실패. deploymentId={}, chunkNo={}, attempt={}",
                request.deploymentId(), request.chunkNo(), request.attempt(), e);

        // 메시지가 없는 예외도 있다. 계약의 message 는 비어 있을 수 없다
        String message = e.getMessage() != null ? e.getMessage() : e.getClass().getName();
        publish(request, new ApplyResultMessage.Rejected(message));
    }

    public void successResponseMessage(ApplyChunkRequest request, AppliedContent applied) {
        log.info("반영 성공. deploymentId={}, chunkNo={}, attempt={}",
                request.deploymentId(), request.chunkNo(), request.attempt());

        publish(request, new ApplyResultMessage.Applied(applied));
    }

    private void publish(ApplyChunkRequest request, ApplyResultMessage.Outcome outcome) {
        dataMigrationResponsePublisher.dataMigrationStatusPublish(new ApplyResultMessage(
                request.deploymentId(), request.chunkNo(), request.attempt(), outcome, Instant.now()));
    }
}
