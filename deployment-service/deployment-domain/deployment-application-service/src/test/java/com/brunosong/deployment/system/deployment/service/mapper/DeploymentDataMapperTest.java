package com.brunosong.deployment.system.deployment.service.mapper;

import com.brunosong.deployment.system.deployment.service.entity.Deployment;
import com.brunosong.deployment.system.deployment.service.outbox.model.DeploymentEventPayload;
import com.brunosong.deployment.system.deployment.service.valueobject.ChunkContent;
import com.brunosong.deployment.system.deployment.service.valueobject.Curriculum;
import com.brunosong.deployment.system.deployment.service.valueobject.DbType;
import com.brunosong.deployment.system.deployment.service.valueobject.SourceConfigId;
import com.brunosong.deployment.system.deployment.service.valueobject.SourceId;
import com.brunosong.deployment.system.deployment.service.valueobject.SourceType;
import com.brunosong.deployment.system.deployment.service.valueobject.TransType;
import com.brunosong.deployment.system.domain.valueobject.TargetId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class DeploymentDataMapperTest {

    private static final ZonedDateTime NOW = ZonedDateTime.parse("2026-11-13T10:00:00Z");

    private final DeploymentDataMapper mapper = new DeploymentDataMapper();

    @Test
    @DisplayName("한 배포의 청크는 뼈대든 과목이든 모두 같은 키를 갖는다")
    void allChunksOfDeploymentShareKey() {
        Curriculum curriculum = curriculum("CUR01");
        List<ChunkContent> chunks = ChunkContent.split(curriculum);
        Deployment deployment = deployment(1L, chunks.size());

        List<String> keys = payloads(deployment, chunks).stream()
                .map(DeploymentEventPayload::messageKey)
                .distinct()
                .toList();

        assertEquals(List.of("ONLINE_CAMPUS:PROD:CUR01"), keys);
    }

    @Test
    @DisplayName("같은 커리큘럼의 다른 배포도 같은 키라 같은 파티션에서 순서대로 처리된다")
    void sameCurriculumAcrossDeploymentsSharesKey() {
        Curriculum curriculum = curriculum("CUR01");
        List<ChunkContent> chunks = ChunkContent.split(curriculum);

        String first = payloads(deployment(1L, chunks.size()), chunks).get(0).messageKey();
        String second = payloads(deployment(1L, chunks.size()), chunks).get(0).messageKey();

        assertEquals(first, second);
    }

    @Test
    @DisplayName("대상이 다르면 키가 다르다")
    void differentTargetHasDifferentKey() {
        Curriculum curriculum = curriculum("CUR01");
        List<ChunkContent> chunks = ChunkContent.split(curriculum);

        String prod = payloads(deployment(1L, chunks.size()), chunks).get(0).messageKey();
        String dev = payloads(deployment(3L, chunks.size()), chunks).get(0).messageKey();

        assertNotEquals(prod, dev);
        assertEquals("ONLINE_CAMPUS:DEV:CUR01", dev);
    }

    private List<DeploymentEventPayload> payloads(Deployment deployment, List<ChunkContent> chunks) {
        return java.util.stream.IntStream.range(0, chunks.size())
                .mapToObj(i -> mapper.toDeploymentEventPayload(deployment, i,
                        chunks.get(i), NOW))
                .toList();
    }

    private Deployment deployment(long targetId, int totalChunkSize) {
        return Deployment.create(new SourceId("6731a2b4c5d6e7f801234567"), new SourceConfigId(1L), "CUR01",
                new TargetId(targetId), TransType.MESSAGING, SourceType.CURRICULUM, DbType.MONGO, totalChunkSize);
    }

    /** 학년 하나, 학기 하나, 과목 둘. 뼈대 1개 + 과목 2개로 나뉜다 */
    private Curriculum curriculum(String curriculumId) {
        Curriculum.Subject math = new Curriculum.Subject("S1", "수학", null, List.of());
        Curriculum.Subject korean = new Curriculum.Subject("S2", "국어", null, List.of());
        Curriculum.Semester semester = new Curriculum.Semester("SEM1", "1학기", null, List.of(math, korean));
        Curriculum.Grade grade = new Curriculum.Grade("G1", "1학년", null, List.of(semester));
        return new Curriculum(curriculumId, "초등 과정", null, List.of(grade));
    }
}
