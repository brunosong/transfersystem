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