package com.brunosong.deployment.system.deployment.service.dataaccess.deployment.adapter;

import com.brunosong.deployment.system.outbox.OutboxStatus;
import com.brunosong.deployment.system.deployment.service.dataaccess.TestJpaConfiguration;
import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.mapper.DeploymentOutboxDataAccessMapperImpl;
import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.repository.DeploymentOutboxJpaRepository;
import com.brunosong.deployment.system.deployment.service.outbox.model.DeploymentOutboxMessage;
import com.brunosong.deployment.system.deployment.service.valueobject.TransType;
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

import static com.brunosong.deployment.system.deployment.service.outbox.model.OutboxMessageType.APPLY_CHUNK_COMMAND;
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
        DeploymentOutboxMessage saved = deploymentOutboxRepository.save(outboxMessage());

        assertEquals(APPLY_CHUNK_COMMAND, saved.getType());
        assertEquals(OutboxStatus.STARTED, saved.getOutboxStatus());
        assertEquals(TransType.MESSAGING, saved.getTransType());
        assertEquals("ONLINE_CAMPUS:PROD:CUR01", saved.getMessageKey());
    }

    /** 스케줄러가 집어 가는 조건 그대로 찾아본다 */
    @Test
    void findByTypeAndOutboxStatus() {
        DeploymentOutboxMessage message = outboxMessage();
        deploymentOutboxRepository.save(message);

        Optional<List<DeploymentOutboxMessage>> found =
                deploymentOutboxRepository.findByTypeAndOutboxStatus(APPLY_CHUNK_COMMAND, OutboxStatus.STARTED);

        assertTrue(found.isPresent());
        assertEquals(1, found.get().size());
        assertEquals(message.getId(), found.get().get(0).getId());
    }

    /** 이미 나간 건은 다음 주기에 다시 집히면 안 된다 */
    @Test
    void completedMessageIsNotPickedUpAgain() {
        DeploymentOutboxMessage message = outboxMessage();
        message.setOutboxStatus(OutboxStatus.COMPLETED);
        deploymentOutboxRepository.save(message);

        Optional<List<DeploymentOutboxMessage>> found =
                deploymentOutboxRepository.findByTypeAndOutboxStatus(APPLY_CHUNK_COMMAND, OutboxStatus.STARTED);

        assertTrue(found.isEmpty() || found.get().isEmpty());
    }

    private DeploymentOutboxMessage outboxMessage() {
        return DeploymentOutboxMessage.builder()
                .id(UUID.randomUUID())
                .type(APPLY_CHUNK_COMMAND)
                .transType(TransType.MESSAGING)
                .payload("{\"deploymentId\":\"" + UUID.randomUUID() + "\"}")
                .messageKey("ONLINE_CAMPUS:PROD:CUR01")
                .createdAt(ZonedDateTime.now(ZoneId.of("UTC")))
                .outboxStatus(OutboxStatus.STARTED)
                .build();
    }
}
