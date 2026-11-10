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