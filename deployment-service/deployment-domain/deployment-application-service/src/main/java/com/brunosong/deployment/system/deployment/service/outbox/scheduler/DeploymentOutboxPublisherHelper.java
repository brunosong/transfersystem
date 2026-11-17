package com.brunosong.deployment.system.deployment.service.outbox.scheduler;

import com.brunosong.deployment.system.deployment.service.outbox.model.DeploymentEventPayload;
import com.brunosong.deployment.system.deployment.service.ports.output.message.publisher.PublishResult;
import com.brunosong.deployment.system.deployment.service.ports.output.repository.ChunkPayload;
import com.brunosong.deployment.system.deployment.service.ports.output.repository.ChunkPayloadRepository;
import com.brunosong.deployment.system.outbox.OutboxStatus;
import com.brunosong.deployment.system.deployment.service.exception.DeploymentDomainException;
import com.brunosong.deployment.system.deployment.service.outbox.model.DeploymentOutboxMessage;
import com.brunosong.deployment.system.deployment.service.ports.output.message.publisher.DeploymentRequestMessagePublisher;
import com.brunosong.deployment.system.deployment.service.ports.output.repository.DeploymentOutboxRepository;
import com.brunosong.deployment.system.deployment.service.valueobject.TransType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeploymentOutboxPublisherHelper {

    private final DeploymentRequestMessagePublisher deploymentRequestMessagePublisher;
    private final ChunkPayloadRepository chunkPayloadRepository;
    private final ObjectMapper objectMapper;

    public PublishResult publish(DeploymentOutboxMessage outboxMessage) {

        TransType transType = outboxMessage.getTransType();

        if (transType == TransType.MESSAGING) {
            return publishMessage(outboxMessage);
        }

        // API 경로는 아직 아웃박스에서 보낼 수 없다. 아웃박스 행에는 Deployment 가 없고
        // DeploymentApiSendHelper 는 Deployment 를 받는다. 포기로 닫지 않으면
        // 다음 주기가 같은 행을 다시 집어 영원히 돈다
        log.error("Outbox publish is not wired for transType: {} (outbox id: {})",
                transType, outboxMessage.getId());
        return PublishResult.GIVE_UP;
    }

    /** 행에는 무엇을 보낼지만 있다. 내용은 접수 때 넣어 둔 내용 표에서 읽어 함께 넘긴다(ADR 0004) */
    private PublishResult publishMessage(DeploymentOutboxMessage outboxMessage) {
        DeploymentEventPayload payload = readPayload(outboxMessage);
        if (payload == null) {
            // 페이로드를 못 읽으면 다시 읽어도 같은 결과다
            return PublishResult.GIVE_UP;
        }

        List<ChunkPayload> found;
        try {
            found = chunkPayloadRepository.findAll(payload.getDeploymentId(), List.of(payload.getChunkOffset()));
        } catch (DataAccessException e) {
            // DB 가 잠깐 안 될 수 있다. 다음 주기에 다시 읽는다
            log.warn("청크 내용을 읽지 못했다. 다음 주기에 다시 읽는다. outbox id: {}", outboxMessage.getId(), e);
            return PublishResult.RETRY_LATER;
        } catch (RuntimeException e) {
            // 저장된 내용을 해석하지 못했다. 다시 읽어도 같다
            log.error("저장된 청크 내용을 읽지 못했다. outbox id: {}", outboxMessage.getId(), e);
            return PublishResult.GIVE_UP;
        }
        if (found.isEmpty()) {
            // 내용이 없으면(정리 작업이 지웠다면) 다시 읽어도 없다
            log.error("청크 내용이 없다. deploymentId: {}, chunkNo: {}, outbox id: {}",
                    payload.getDeploymentId(), payload.getChunkOffset(), outboxMessage.getId());
            return PublishResult.GIVE_UP;
        }

        return deploymentRequestMessagePublisher.publish(outboxMessage, payload, found.get(0).content());
    }

    private DeploymentEventPayload readPayload(DeploymentOutboxMessage outboxMessage) {
        try {
            return objectMapper.readValue(outboxMessage.getPayload(), DeploymentEventPayload.class);
        } catch (JsonProcessingException e) {
            log.error("Could not read DeploymentEventPayload for outbox id: {}", outboxMessage.getId(), e);
            return null;
        }
    }

}
