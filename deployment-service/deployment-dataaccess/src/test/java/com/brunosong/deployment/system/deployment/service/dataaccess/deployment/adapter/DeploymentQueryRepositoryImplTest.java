package com.brunosong.deployment.system.deployment.service.dataaccess.deployment.adapter;

import com.brunosong.deployment.system.domain.valueobject.DeploymentStatus;
import com.brunosong.deployment.system.deployment.service.dto.query.ChunkRecord;
import com.brunosong.deployment.system.deployment.service.dto.query.PageView;
import com.brunosong.deployment.system.deployment.service.dto.query.SourceConfigView;
import com.brunosong.deployment.system.deployment.service.dto.query.DeploymentRecord;
import com.brunosong.deployment.system.deployment.service.dto.query.DeploymentSearchCondition;
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

import java.time.Instant;
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
@Sql({"/sql/deployment-query-schema.sql", "/sql/deployment-query-chunks.sql"})
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=none")
class DeploymentQueryRepositoryImplTest {

    private static final String DATAACCESS = "com.brunosong.deployment.system.deployment.service.dataaccess";

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

    /**
     * 확인하는 것: 상세 화면의 청크는 새 청크 표에서, 번호 순서대로, 새 상태 이름으로 온다.
     *
     * 옛 표는 결과가 와야 행이 생겨 응답을 기다리는 청크가 화면에서 빠졌다.
     * 새 표는 접수할 때 모든 청크를 만들므로 SENT 인 과목 2~4도 보여야 한다.
     */
    @Test
    @DisplayName("청크는 새 표에서 번호 순서대로, 새 상태 이름으로 돌려준다")
    void chunksInOrder() {
        // 실행: 진행 중인 배포(1111)의 청크를 읽는다
        List<ChunkRecord> chunks = repository.findChunks(IN_PROGRESS);

        // 확인 1: 결과가 안 온 청크까지 다섯 개가 모두, 번호 순서대로 온다
        assertThat(chunks).extracting(ChunkRecord::chunkOffset).containsExactly(0, 1, 2, 3, 4);
        // 확인 2: 상태는 옛 "SUCCESS"가 아니라 새 표의 이름 그대로다
        assertThat(chunks).extracting(ChunkRecord::status)
                .containsExactly("APPLIED", "APPLIED", "SENT", "SENT", "SENT");
        // 확인 3: 실패하지 않은 청크의 시각은 보낸 시각이다
        assertThat(chunks.get(2).processedAt()).isEqualTo(Instant.parse("2026-11-01T10:00:02Z"));
    }

    @Test
    @DisplayName("원본 설정은 계정 없이, 코드에 없는 열거형 값이 있어도 모두 돌려준다")
    void sourceConfigs() {
        List<SourceConfigView> configs = repository.findSourceConfigs();

        assertThat(configs).extracting(SourceConfigView::sourceType)
                .containsExactly("CURRICULUM", "LEARNING_MATERIAL");
        assertThat(configs.get(0).tableOrCollection()).isEqualTo("CurriculumData");
        assertThat(configs.get(0).name()).isEqualTo("learningDB.CurriculumData");
        assertThat(configs).allMatch(SourceConfigView::enabled);
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

    /**
     * 확인하는 것: 실패한 청크는 실패 사유와 실패 시각을, 안 보낸 청크는 시각 없이 시도 0을 돌려준다.
     *
     * 옛 표에는 실패 사유 칸이 없어 화면의 오류 메시지가 늘 비어 있었다.
     * 시도 번호는 화면의 재전송 횟수(시도 - 1)를 만드는 데 쓴다.
     */
    @Test
    @DisplayName("실패한 청크는 사유와 실패 시각을, 안 보낸 청크는 시각 없이 돌려준다")
    void failedAndWaitingChunks() {
        // 실행: 뼈대가 거절된 배포(2222)의 청크를 읽는다
        List<ChunkRecord> chunks = repository.findChunks(FAILED);

        // 확인 1: 뼈대는 실패이고, 대상이 보낸 사유와 실패한 시각이 함께 온다
        ChunkRecord skeleton = chunks.get(0);
        assertThat(skeleton.status()).isEqualTo("FAILED");
        assertThat(skeleton.errorMessage()).isEqualTo("Duplicate entry");
        assertThat(skeleton.processedAt()).isEqualTo(Instant.parse("2026-11-01T11:00:09Z"));
        assertThat(skeleton.attempt()).isEqualTo(1);

        // 확인 2: 뼈대가 반영되지 않아 과목은 아직 안 보냈다. 시각이 없고 시도 번호는 0이다
        ChunkRecord subject = chunks.get(1);
        assertThat(subject.status()).isEqualTo("WAITING");
        assertThat(subject.processedAt()).isNull();
        assertThat(subject.attempt()).isZero();
    }

}
