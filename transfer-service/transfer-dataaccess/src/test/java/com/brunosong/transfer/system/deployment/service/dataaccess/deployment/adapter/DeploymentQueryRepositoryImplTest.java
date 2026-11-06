package com.brunosong.transfer.system.deployment.service.dataaccess.deployment.adapter;

import com.brunosong.transfer.system.domain.valueobject.DeploymentStatus;
import com.brunosong.transfer.system.deployment.service.dto.query.ChunkRecord;
import com.brunosong.transfer.system.deployment.service.dto.query.PageView;
import com.brunosong.transfer.system.deployment.service.dto.query.SourceConfigView;
import com.brunosong.transfer.system.deployment.service.dto.query.DeploymentRecord;
import com.brunosong.transfer.system.deployment.service.dto.query.DeploymentSearchCondition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 조회 어댑터를 실제 Postgres 에서 검증한다. 열거형 타입 컬럼, 상태 문자열 대소문자,
 * 청크 집계 쿼리는 H2 로는 운영과 같은 결과를 보장할 수 없다.
 */
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = DeploymentQueryRepositoryImplTest.Config.class)
@Sql("/sql/deployment-query-schema.sql")
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=none")
class DeploymentQueryRepositoryImplTest {

    private static final String DATAACCESS = "com.brunosong.transfer.system.deployment.service.dataaccess";

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18-alpine");

    @Configuration
    @EnableJpaAuditing
    @EnableJpaRepositories(basePackages = {DATAACCESS + ".deployment.repository", DATAACCESS + ".source.repository"})
    @EntityScan(basePackages = {DATAACCESS + ".deployment.entity", DATAACCESS + ".source.entity"})
    @Import(DeploymentQueryRepositoryImpl.class)
    static class Config {
    }

    private static final UUID IN_PROGRESS = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID FAILED = UUID.fromString("22222222-2222-2222-2222-222222222222");

    @Autowired
    DeploymentQueryRepositoryImpl repository;

    @Test
    @DisplayName("목록은 최신순이고 배포마다 청크 성공·실패 수를 모아 온다")
    void listWithChunkCounts() {
        PageView<DeploymentRecord> page = repository.findDeployments(new DeploymentSearchCondition(null, 0, 20));

        assertThat(page.totalItems()).isEqualTo(3);
        assertThat(page.items()).extracting(DeploymentRecord::sourceId)
                .containsExactly("curriculum-003", "curriculum-002", "curriculum-001");

        DeploymentRecord inProgress = page.items().get(2);
        assertThat(inProgress.totalChunks()).isEqualTo(5);
        assertThat(inProgress.succeededChunks()).isEqualTo(2);
        assertThat(inProgress.failedChunks()).isZero();

        DeploymentRecord failed = page.items().get(1);
        assertThat(failed.failedChunks()).isEqualTo(1);
    }

    @Test
    @DisplayName("상태로 거르고 페이지를 나눈다")
    void filterAndPage() {
        PageView<DeploymentRecord> failed = repository.findDeployments(new DeploymentSearchCondition(DeploymentStatus.FAILED, 0, 20));
        assertThat(failed.items()).extracting(DeploymentRecord::deploymentId).containsExactly(FAILED);

        PageView<DeploymentRecord> firstPage = repository.findDeployments(new DeploymentSearchCondition(null, 0, 2));
        assertThat(firstPage.items()).hasSize(2);
        assertThat(firstPage.totalPages()).isEqualTo(2);
    }

    @Test
    @DisplayName("청크는 순서대로, 상태는 대문자로 돌려준다")
    void chunksInOrder() {
        List<ChunkRecord> chunks = repository.findChunks(IN_PROGRESS);

        assertThat(chunks).extracting(ChunkRecord::chunkOffset).containsExactly(1, 2);
        assertThat(chunks).extracting(ChunkRecord::status).containsOnly("SUCCESS");
        assertThat(chunks).allSatisfy(c -> assertThat(c.processedAt()).isNotNull());
    }

    @Test
    @DisplayName("원본 설정은 계정 없이, 코드에 없는 열거형 값이 있어도 모두 돌려준다")
    void sourceConfigs() {
        List<SourceConfigView> configs = repository.findSourceConfigs();

        assertThat(configs).extracting(SourceConfigView::sourceType)
                .containsExactly("CURRICULUM", "LEARNING_MATERIAL");
        assertThat(configs.get(0).tableOrCollection()).isEqualTo("CurriculumData");
    }

    @Test
    @DisplayName("상태별 배포 수를 센다")
    void countByStatus() {
        assertThat(repository.countByStatus()).containsOnly(
                Map.entry(DeploymentStatus.SENT, 1L),
                Map.entry(DeploymentStatus.FAILED, 1L),
                Map.entry(DeploymentStatus.SUCCESS, 1L));
    }

    @Test
    @DisplayName("없는 배포는 비어 있다")
    void notFound() {
        assertThat(repository.findDeployment(UUID.randomUUID())).isEmpty();
    }
}
