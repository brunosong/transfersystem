package com.brunosong.deployment.system.deployment.service.dataaccess.deployment.adapter;

import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.entity.ChunkPayloadEntity;
import com.brunosong.deployment.system.deployment.service.dataaccess.deployment.repository.ChunkPayloadJpaRepository;
import com.brunosong.deployment.system.deployment.service.ports.output.repository.ChunkPayload;
import com.brunosong.deployment.system.deployment.service.valueobject.ChunkContent;
import com.brunosong.deployment.system.deployment.service.valueobject.Curriculum;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = ChunkPayloadRepositoryImplTest.Config.class)
@Sql("/sql/deployment-query-schema.sql")
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=none")
class ChunkPayloadRepositoryImplTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18-alpine");

    @Configuration
    @EnableJpaRepositories(basePackageClasses = ChunkPayloadJpaRepository.class)
    @EntityScan(basePackageClasses = ChunkPayloadEntity.class)
    @Import(ChunkPayloadRepositoryImpl.class)
    static class Config {
        // @DataJpaTest 는 Jackson 자동 설정을 올리지 않아서 직접 만든다
        @Bean
        ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }

    /** 스키마 파일이 미리 넣어 둔 배포. 내용은 배포가 있어야 저장된다(외래 키) */
    private static final UUID DEPLOYMENT_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Autowired
    private ChunkPayloadRepositoryImpl chunkPayloadRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    @DisplayName("저장한 뼈대와 과목을 읽으면 종류와 내용이 그대로다")
    void savedContentsAreReadBackAsTheyWere() {
        List<ChunkContent> chunks = ChunkContent.split(curriculum());
        chunkPayloadRepository.saveAll(DEPLOYMENT_ID, chunks);
        em.flush();
        em.clear();

        List<ChunkPayload> found = chunkPayloadRepository.findAll(DEPLOYMENT_ID, List.of(0, 1, 2));

        assertEquals(3, found.size());
        assertInstanceOf(ChunkContent.Skeleton.class, found.get(0).content());
        assertInstanceOf(ChunkContent.SubjectChunk.class, found.get(1).content());
        for (int no = 0; no < 3; no++) {
            assertEquals(no, found.get(no).chunkNo());
            assertEquals(chunks.get(no), found.get(no).content());   // 레코드라 값이 같으면 같다
        }
    }

    @Test
    @DisplayName("고른 번호만 번호 순서로 돌려준다")
    void returnsOnlyRequestedNumbersInOrder() {
        chunkPayloadRepository.saveAll(DEPLOYMENT_ID, ChunkContent.split(curriculum()));
        em.flush();
        em.clear();

        List<ChunkPayload> found = chunkPayloadRepository.findAll(DEPLOYMENT_ID, List.of(2, 1));

        assertEquals(List.of(1, 2), found.stream().map(ChunkPayload::chunkNo).toList());
    }


    @Test
    @DisplayName("성공한 배포와 더 최근 배포가 있는 실패 배포의 내용만 지운다")
    void deleteNotRetryableRemovesOnlyContentThatCannotBeResent() {
        // 확인하는 것: 재전송할 수 없는 배포의 내용만 지우고, 재전송할 수 있는 배포의 내용은 남기는지

        // 준비: 배포를 상태, 커리큘럼, 대상, 만든 시각을 바꿔 가며 넣고, 배포마다 내용 한 줄씩 넣는다
        UUID succeeded = deployment("SUCCEEDED", "CUR-A", 1, "10:00");        // 지운다: 끝났다
        UUID olderFailed = deployment("FAILED", "CUR-B", 1, "10:00");         // 지운다: 같은 커리큘럼 × 대상에 더 최근 배포가 있다
        UUID latestFailed = deployment("FAILED", "CUR-B", 1, "11:00");        // 남긴다: 가장 최근이라 재전송할 수 있다
        UUID otherTargetFailed = deployment("FAILED", "CUR-B", 2, "09:00");   // 남긴다: 더 최근 배포는 다른 대상의 것이다
        UUID inProgress = deployment("IN_PROGRESS", "CUR-C", 1, "10:00");     // 남긴다: 아직 진행 중이다
        UUID legacyFailed = deployment("FAILED", null, 3, "10:00");           // 남긴다: 커리큘럼 ID가 없는 옛 배포는 판단하지 않는다
        deployment("FAILED", null, 3, "11:00");                                // 위 옛 배포보다 최근인 옛 배포. 내용은 넣지 않는다
        List.of(succeeded, olderFailed, latestFailed, otherTargetFailed, inProgress, legacyFailed)
                .forEach(this::payload);

        // 실행
        int deleted = chunkPayloadRepository.deleteNotRetryable();

        // 확인: 두 배포의 내용이 지워졌고, 나머지 네 배포의 내용은 남았다
        assertEquals(2, deleted);
        assertEquals(java.util.Set.of(latestFailed, otherTargetFailed, inProgress, legacyFailed), remainingDeploymentIds());
    }

    /**
     * 배포 한 건을 SQL 로 바로 넣는다. 도메인 객체로 만들면 상태를 마음대로 정할 수 없다.
     * 열거형 칸(source_type, db_type)은 바인딩한 문자열이 열거형으로 바뀌지 않아 SQL 에 글자로 적는다
     */
    private UUID deployment(String status, String curriculumId, long targetId, String createdAtTime) {
        UUID id = UUID.randomUUID();
        em.getEntityManager().createNativeQuery("""
                        INSERT INTO deployment (id, source_id, sourceconfig_id, curriculum_id, source_type, target_id,
                                                db_type, trans_type, deployment_status, create_admin_id, created_at)
                        VALUES (CAST(:id AS uuid), 'source', 1, :curriculumId, 'CURRICULUM', :targetId,
                                'MONGO', 'MESSAGING', :status, 'tester', CAST(:createdAt AS timestamp))
                        """)
                .setParameter("id", id.toString())
                .setParameter("curriculumId", curriculumId)
                .setParameter("targetId", targetId)
                .setParameter("status", status)
                .setParameter("createdAt", "2026-11-10 " + createdAtTime + ":00")
                .executeUpdate();
        return id;
    }

    /** 배포에 내용 한 줄(0번 청크)을 넣는다. 지우는 쿼리는 내용을 보지 않아 빈 JSON 이면 된다 */
    private void payload(UUID deploymentId) {
        em.getEntityManager().createNativeQuery(
                        "INSERT INTO deployment_chunk_payload (deployment_id, chunk_no, content) VALUES (CAST(:id AS uuid), 0, CAST('{}' AS jsonb))")
                .setParameter("id", deploymentId.toString())
                .executeUpdate();
    }

    /** 내용이 남아 있는 배포 ID 들 */
    private java.util.Set<UUID> remainingDeploymentIds() {
        List<?> rows = em.getEntityManager()
                .createNativeQuery("SELECT CAST(deployment_id AS varchar) FROM deployment_chunk_payload")
                .getResultList();
        return rows.stream().map(row -> UUID.fromString((String) row)).collect(java.util.stream.Collectors.toSet());
    }

        /** 학년 하나, 학기 하나, 과목 둘. 뼈대 1개 + 과목 2개로 나뉜다 */
    private static Curriculum curriculum() {
        Curriculum.Lesson lesson = new Curriculum.Lesson("L1", "덧셈", null, 1, 1, "{\"level\":1}");
        Curriculum.Unit unit = new Curriculum.Unit("U1", "수와 연산", null, null, List.of(lesson));
        Curriculum.Subject math = new Curriculum.Subject("S1", "수학", null, List.of(unit));
        Curriculum.Subject korean = new Curriculum.Subject("S2", "국어", null, List.of());
        Curriculum.Semester semester = new Curriculum.Semester("SEM1", "1학기", null, List.of(math, korean));
        Curriculum.Grade grade = new Curriculum.Grade("G1", "1학년", null, List.of(semester));
        return new Curriculum("CUR01", "초등 과정", null, List.of(grade));
    }
}