package com.brunosong.deployment.system.deployment.service.dataaccess.deployment.adapter;

import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.entity.DeploymentEntity;
import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.mapper.DeploymentDataAccessMapper;
import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.repository.DeploymentJpaRepository;
import com.brunosong.deployment.system.deployment.service.entity.Chunk;
import com.brunosong.deployment.system.deployment.service.entity.Deployment;
import com.brunosong.deployment.system.deployment.service.valueobject.*;
import com.brunosong.deployment.system.domain.valueobject.DeploymentId;
import com.brunosong.deployment.system.domain.valueobject.DeploymentStatus;
import com.brunosong.deployment.system.domain.valueobject.TargetId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 배포를 청크와 함께 저장하고 되살리는 어댑터를 실제 Postgres 에서 확인한다.
 * 트랜잭션 경계와 낙관적 락을 보려고 테스트의 기본 트랜잭션을 끄고 직접 연다.
 */
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = DeploymentRepositoryImplTest.Config.class)
@Sql("/sql/deployment-repository-schema.sql")
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=none")
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class DeploymentRepositoryImplTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18-alpine");

    @Configuration
    @EnableJpaAuditing
    @EnableJpaRepositories(basePackageClasses = DeploymentJpaRepository.class)
    @EntityScan(basePackageClasses = DeploymentEntity.class)
    @Import({DeploymentRepositoryImpl.class, DeploymentDataAccessMapper.class})
    static class Config {
    }

    private static final UUID DEPLOYMENT_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private static final Instant NOW = Instant.parse("2026-11-10T10:00:00Z");

    @Autowired
    private DeploymentRepositoryImpl repository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private TransactionTemplate tx;

    @BeforeEach
    void setUp() {
        tx = new TransactionTemplate(transactionManager);
        tx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    @Test
    @DisplayName("저장한 배포를 다시 읽으면 청크 상태, 시도 번호, 실패 이유가 그대로다")
    void saveDeploymentIsRestoredWithChunks() {
        Deployment deployment = subjectsSent();
        deployment.recordFailure(1, 1, "학기가 없습니다", NOW.plusSeconds(3));
        save(deployment);

        Deployment found = find();

        assertEquals(DeploymentStatus.IN_PROGRESS, found.getDeploymentStatus());

        List<Chunk> chunks = found.getChunks();
        assertEquals(3, chunks.size());
        assertEquals(ChunkStatus.APPLIED, chunks.get(0).getStatus());

        Chunk failed = chunks.get(1);
        assertEquals(ChunkStatus.FAILED, failed.getStatus());
        assertEquals(1, failed.getAttempt());
        assertEquals(Failure.rejected("학기가 없습니다", NOW.plusSeconds(3)), failed.getFailure());

        Chunk sent = chunks.get(2);
        assertEquals(ChunkStatus.SENT, sent.getStatus());
        assertEquals(NOW, sent.getSentAt());
    }

    @Test
    @DisplayName("읽은 배포에 결과를 기록하고 저장하면 청크와 배포 상태가 함께 바뀐다")
    void resultOnRestoredDeploymentIsSaved() {
        Deployment deployment = subjectsSent();
        deployment.recordFailure(1, 1, "학기가 없습니다", NOW);
        save(deployment);

        tx.executeWithoutResult(status -> {
            Deployment found = repository.findById(DEPLOYMENT_ID).orElseThrow();
            found.recordSuccess(2, NOW);
            repository.save(found);
        });

        Deployment found = find();
        assertEquals(ChunkStatus.APPLIED, found.getChunks().get(2).getStatus());
        assertEquals(DeploymentStatus.FAILED, found.getDeploymentStatus());
    }

    @Test
    @DisplayName("같은 배포에 두 결과가 동시에 오면 나중에 저장하는 쪽이 실패하고 아무것도 남기지 않는다")
    void concurrentResultsOnSameDeploymentConflict() {
        save(subjectsSent());

        assertThrows(OptimisticLockingFailureException.class, () -> tx.executeWithoutResult(first -> {
            Deployment a = repository.findById(DEPLOYMENT_ID).orElseThrow();

            tx.executeWithoutResult(second -> {
                Deployment b = repository.findById(DEPLOYMENT_ID).orElseThrow();
                b.recordSuccess(1, NOW);
                repository.save(b);
            });

            a.recordSuccess(2, NOW);
            repository.save(a);
        }));

        Deployment found = find();
        assertEquals(ChunkStatus.APPLIED, found.getChunks().get(1).getStatus());
        assertEquals(ChunkStatus.SENT, found.getChunks().get(2).getStatus());
    }

    private void save(Deployment deployment) {
        tx.executeWithoutResult(status -> repository.save(deployment));
    }

    private Deployment find() {
        return tx.execute(status -> repository.findById(DEPLOYMENT_ID).orElseThrow());
    }

    /** 뼈대가 반영되어 과목 둘이 나간 배포 */
    private static Deployment subjectsSent() {
        Deployment deployment = Deployment.builder()
                .id(new DeploymentId(DEPLOYMENT_ID))
                .sourceId(new SourceId("curriculum-001"))
                .sourceConfigId(new SourceConfigId(1L))
                .targetId(new TargetId(1L))
                .transType(TransType.MESSAGING)
                .sourceType(SourceType.CURRICULUM)
                .dbType(DbType.MONGO)
                .totalChunkSize(3)
                .build();
        deployment.accept(2, NOW);
        deployment.recordSuccess(0, NOW);
        return deployment;
    }

}