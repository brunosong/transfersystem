package com.brunosong.deployment.system.deployment.service.dataaccess.deployment.repository;

import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.entity.ChunkEntity;
import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.entity.ChunkEntityId;
import com.brunosong.deployment.system.deployment.service.valueobject.ChunkStatus;
import com.brunosong.deployment.system.deployment.service.valueobject.FailureKind;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = ChunkJpaRepositoryTest.Config.class)
@Sql("/sql/deployment-query-schema.sql")
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=none")
class ChunkJpaRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18-alpine");

    @Configuration
    @EnableJpaAuditing
    @EnableJpaRepositories(basePackageClasses = ChunkJpaRepository.class)
    @EntityScan(basePackageClasses = ChunkEntity.class)
    static class Config {
    }

    /** 스키마 파일이 미리 넣어 둔 배포. 청크는 배포가 있어야 저장된다(외래 키) */
    private static final UUID DEPLOYMENT_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID OTHER_DEPLOYMENT_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID THIRD_DEPLOYMENT_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");

    private static final Instant SENT_AT = Instant.parse("2026-11-10T10:00:00Z");

    @Autowired
    private ChunkJpaRepository chunkJpaRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    @DisplayName("저장한 청크를 번호 순서로 읽으면 칸이 모두 그대로다")
    void savedChunksAreReadInOrderWithAllColumns() {
        chunkJpaRepository.saveAll(List.of(
                chunk(1, ChunkStatus.FAILED, 2, SENT_AT, FailureKind.REJECTED, "학기가 없습니다", SENT_AT.plusSeconds(3)),
                chunk(0, ChunkStatus.APPLIED, 1, SENT_AT, null, null, null)
        ));
        em.flush();
        em.clear();

        List<ChunkEntity> chunks = chunkJpaRepository.findAllById_DeploymentIdOrderById_ChunkNoAsc(DEPLOYMENT_ID);

        assertEquals(2, chunks.size());
        assertEquals(0, chunks.getFirst().getId().getChunkNo());

        ChunkEntity failed = chunks.get(1);
        assertEquals(ChunkStatus.FAILED, failed.getStatus());
        assertEquals(2, failed.getAttempt());
        assertEquals(SENT_AT, failed.getSentAt());
        assertEquals(FailureKind.REJECTED, failed.getFailureKind());
        assertEquals("학기가 없습니다", failed.getFailureMessage());
        assertEquals(SENT_AT.plusSeconds(3), failed.getFailedAt());
    }

    @Test
    @DisplayName("같은 청크를 다시 저장하면 행이 늘지 않고 고쳐진다")
    void savingSameChunkAgainUpdatesRow() {
        chunkJpaRepository.save(chunk(0, ChunkStatus.SENT, 1, SENT_AT, null, null, null));
        em.flush();
        em.clear();

        chunkJpaRepository.save(chunk(0, ChunkStatus.APPLIED, 1, SENT_AT, null, null, null));
        em.flush();
        em.clear();

        List<ChunkEntity> chunks = chunkJpaRepository.findAllById_DeploymentIdOrderById_ChunkNoAsc(DEPLOYMENT_ID);
        assertEquals(1, chunks.size());
        assertEquals(ChunkStatus.APPLIED, chunks.getFirst().getStatus());

    }

    @Test
    @DisplayName("기한 전에 보낸 뒤 아직 응답을 기다리는 청크가 있는 배포만, 한 번씩 찾는다")
    void findsDeploymentsWaitingForReplyTooLong() {
        Instant cutoff = SENT_AT;   // 이 시각 전에 보낸 SENT 청크가 기한을 넘긴 것이다
        chunkJpaRepository.saveAll(List.of(
                // 배포 1: 기한을 넘긴 SENT 청크가 둘 → 배포 ID는 한 번만 나와야 한다
                chunk(DEPLOYMENT_ID, 1, ChunkStatus.SENT, SENT_AT.minusSeconds(60)),
                chunk(DEPLOYMENT_ID, 2, ChunkStatus.SENT, SENT_AT.minusSeconds(30)),
                // 배포 2: SENT 지만 기한 안에 보냈다
                chunk(OTHER_DEPLOYMENT_ID, 0, ChunkStatus.SENT, SENT_AT.plusSeconds(10)),
                // 배포 3: 오래됐지만 이미 결과를 받았다
                chunk(THIRD_DEPLOYMENT_ID, 0, ChunkStatus.APPLIED, SENT_AT.minusSeconds(600)),
                chunk(THIRD_DEPLOYMENT_ID, 1, ChunkStatus.FAILED, SENT_AT.minusSeconds(600))
        ));
        em.flush();
        em.clear();

        assertEquals(List.of(DEPLOYMENT_ID), chunkJpaRepository.findDeploymentIdsWaitingForReplySentBefore(cutoff));
    }

    private static ChunkEntity chunk(UUID deploymentId, int no, ChunkStatus status, Instant sentAt) {
        return ChunkEntity.builder()
                .id(new ChunkEntityId(deploymentId, no))
                .status(status)
                .attempt(1)
                .sentAt(sentAt)
                .build();
    }

    private static ChunkEntity chunk(int no, ChunkStatus status, int attempt, Instant sentAt,
                                     FailureKind failureKind, String failureMessage, Instant failedAt) {
        return ChunkEntity.builder()
                .id(new ChunkEntityId(DEPLOYMENT_ID, no))
                .status(status)
                .attempt(attempt)
                .sentAt(sentAt)
                .failureKind(failureKind)
                .failureMessage(failureMessage)
                .failedAt(failedAt)
                .build();
    }

}