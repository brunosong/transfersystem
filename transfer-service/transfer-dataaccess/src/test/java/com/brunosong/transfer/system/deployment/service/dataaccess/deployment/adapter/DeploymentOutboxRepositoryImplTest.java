package com.brunosong.transfer.system.deployment.service.dataaccess.deployment.adapter;

import com.brunosong.transfer.system.outbox.OutboxStatus;
import com.brunosong.transfer.system.saga.SagaStatus;
import com.brunosong.transfer.system.deployment.service.dataaccess.TestJpaConfiguration;
import com.brunosong.transfer.system.deployment.service.dataaccess.deployment.mapper.DeploymentOutboxDataAccessMapperImpl;
import com.brunosong.transfer.system.deployment.service.dataaccess.deployment.repository.DeploymentOutboxJpaRepository;
import com.brunosong.transfer.system.deployment.service.outbox.model.DeploymentOutboxMessage;
import com.brunosong.transfer.system.deployment.service.valueobject.TransType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.brunosong.transfer.system.saga.deployment.SagaConstants.DEPLOYMENT_SAGA_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// 매퍼는 MapStruct 가 만든 구현체를 올려야 한다. 인터페이스를 적으면 빈이 없다
@ContextConfiguration(classes = { TestJpaConfiguration.class , DeploymentOutboxRepositoryImpl.class ,
                                    DeploymentOutboxJpaRepository.class, DeploymentOutboxDataAccessMapperImpl.class })
@TestPropertySource(properties = {"spring.jpa.show-sql=true"})
@DataJpaTest
class DeploymentOutboxRepositoryImplTest {

    @Autowired
    private DeploymentOutboxRepositoryImpl deploymentOutboxRepository;

    @Test
    void save() {
        DeploymentOutboxMessage saved = deploymentOutboxRepository.save(outboxMessage(UUID.randomUUID()));

        assertEquals(DEPLOYMENT_SAGA_NAME, saved.getType());
        assertEquals(OutboxStatus.STARTED, saved.getOutboxStatus());
        assertEquals(TransType.MESSAGING, saved.getTransType());
    }

    /** 스케줄러가 집어 가는 조건 그대로 찾아본다 */
    @Test
    void findByTypeAndOutboxStatusAndSagaStatus() {
        UUID sagaId = UUID.randomUUID();
        deploymentOutboxRepository.save(outboxMessage(sagaId));

        Optional<List<DeploymentOutboxMessage>> found =
                deploymentOutboxRepository.findByTypeAndOutboxStatusAndSagaStatus(DEPLOYMENT_SAGA_NAME,
                        OutboxStatus.STARTED, SagaStatus.STARTED, SagaStatus.COMPENSATED);

        assertTrue(found.isPresent());
        assertEquals(1, found.get().size());
        assertEquals(sagaId, found.get().get(0).getSagaId());
    }

    /** 이미 나간 건은 다음 주기에 다시 집히면 안 된다 */
    @Test
    void completedMessageIsNotPickedUpAgain() {
        DeploymentOutboxMessage message = outboxMessage(UUID.randomUUID());
        message.setOutboxStatus(OutboxStatus.COMPLETED);
        deploymentOutboxRepository.save(message);

        Optional<List<DeploymentOutboxMessage>> found =
                deploymentOutboxRepository.findByTypeAndOutboxStatusAndSagaStatus(DEPLOYMENT_SAGA_NAME,
                        OutboxStatus.STARTED, SagaStatus.STARTED, SagaStatus.COMPENSATED);

        assertTrue(found.isEmpty() || found.get().isEmpty());
    }

    private DeploymentOutboxMessage outboxMessage(UUID sagaId) {
        return DeploymentOutboxMessage.builder()
                .id(UUID.randomUUID())
                .sagaId(sagaId)
                .type(DEPLOYMENT_SAGA_NAME)
                .transType(TransType.MESSAGING)
                .payload("{\"deploymentId\":\"" + UUID.randomUUID() + "\"}")
                .createdAt(ZonedDateTime.now(ZoneId.of("UTC")))
                .sagaStatus(SagaStatus.STARTED)
                .outboxStatus(OutboxStatus.STARTED)
                .build();
    }
}
