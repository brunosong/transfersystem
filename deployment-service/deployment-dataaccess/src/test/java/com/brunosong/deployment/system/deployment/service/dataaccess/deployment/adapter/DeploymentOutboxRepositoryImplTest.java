package com.brunosong.deployment.system.deployment.service.dataaccess.deployment.adapter;

import com.brunosong.deployment.system.outbox.OutboxStatus;
import com.brunosong.deployment.system.deployment.service.dataaccess.TestJpaConfiguration;
import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.mapper.DeploymentOutboxDataAccessMapperImpl;
import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.repository.DeploymentOutboxJpaRepository;
import com.brunosong.deployment.system.deployment.service.outbox.model.DeploymentOutboxMessage;
import com.brunosong.deployment.system.deployment.service.valueobject.TransType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.brunosong.deployment.system.deployment.service.outbox.model.OutboxMessageType.APPLY_CHUNK_COMMAND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = { TestJpaConfiguration.class , DeploymentOutboxRepositoryImpl.class ,
        DeploymentOutboxJpaRepository.class, DeploymentOutboxDataAccessMapperImpl.class })
@Sql("/sql/deployment-outbox-schema.sql")
@TestPropertySource(properties = {"spring.jpa.show-sql=true", "spring.jpa.hibernate.ddl-auto=none"})
class DeploymentOutboxRepositoryImplTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18-alpine");

    @Autowired
    private DeploymentOutboxRepositoryImpl deploymentOutboxRepository;

    @Autowired
    private DeploymentOutboxJpaRepository deploymentOutboxJpaRepository;

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

    /** UUID 는 무작위라, 순번이 없으면 넣은 순서와 읽는 순서가 어긋난다 */
    @Test
    void startedRowsAreReadInInsertOrder() {
        List<UUID> inserted = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            DeploymentOutboxMessage message = outboxMessage();
            inserted.add(message.getId());
            deploymentOutboxRepository.save(message);
        }

        List<UUID> found = deploymentOutboxRepository
                .findByTypeAndOutboxStatus(APPLY_CHUNK_COMMAND, OutboxStatus.STARTED).orElseThrow()
                .stream().map(DeploymentOutboxMessage::getId).toList();

        assertEquals(inserted, found);
    }

    /** 밀린 행이 많아도 한 주기가 너무 길어지지 않게 한 번에 100개까지만 읽는다 */
    @Test
    void readsAtMost100RowsAtOnce() {
        for (int i = 0; i < 101; i++) {
            deploymentOutboxRepository.save(outboxMessage());
        }

        assertEquals(100, deploymentOutboxRepository
                .findByTypeAndOutboxStatus(APPLY_CHUNK_COMMAND, OutboxStatus.STARTED).orElseThrow().size());
    }

    @Test
    @DisplayName("처리가 끝나고 기준 시각보다 오래된 COMPLETED, FAILED 행만 지우고, STARTED 와 최근 행은 남긴다")
    void deleteProcessedBeforeRemovesOnlyOldFinishedRows() {
        // 확인하는 것: 정리 작업이 지울 행을 상태와 처리 시각 둘 다로 고르는지

        // 준비: 기준은 "지금 - 7일". 그보다 하루 이른 8일 전과, 하루 전을 쓴다
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        ZonedDateTime before = now.minusDays(7);
        ZonedDateTime old = now.minusDays(8);
        ZonedDateTime recent = now.minusDays(1);

        DeploymentOutboxMessage oldCompleted = processed(OutboxStatus.COMPLETED, old);     // 지운다
        DeploymentOutboxMessage oldFailed = processed(OutboxStatus.FAILED, old);           // 지운다
        DeploymentOutboxMessage recentCompleted = processed(OutboxStatus.COMPLETED, recent); // 남긴다: 아직 7일이 안 됐다
        // 남긴다: 아직 보내지 않은 행. 평소엔 처리 시각이 없지만, 상태로도 걸러지는지 보려고 일부러 옛 시각을 넣는다
        DeploymentOutboxMessage oldStarted = processed(OutboxStatus.STARTED, old);
        List.of(oldCompleted, oldFailed, recentCompleted, oldStarted).forEach(deploymentOutboxRepository::save);

        // 실행
        int deleted = deploymentOutboxRepository.deleteProcessedBefore(before);

        // 확인: 두 행이 지워졌고, 남은 것은 최근 COMPLETED 와 STARTED 다
        assertEquals(2, deleted);
        List<UUID> remaining = deploymentOutboxJpaRepository.findAll().stream().map(e -> e.getId()).toList();
        assertEquals(java.util.Set.of(recentCompleted.getId(), oldStarted.getId()), java.util.Set.copyOf(remaining));
    }

    /** 상태와 처리 시각을 정한 행. 처리 시각은 스케줄러가 행을 닫을 때 찍는 값이다 */
    private DeploymentOutboxMessage processed(OutboxStatus status, ZonedDateTime processedAt) {
        DeploymentOutboxMessage message = outboxMessage();
        message.setOutboxStatus(status);
        message.setProcessedAt(processedAt);
        return message;
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
