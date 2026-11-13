package com.brunosong.deployment.system.deployment.service.entity;

import com.brunosong.deployment.system.deployment.service.exception.DeploymentDomainException;
import com.brunosong.deployment.system.deployment.service.valueobject.ChunkStatus;
import com.brunosong.deployment.system.deployment.service.valueobject.DbType;
import com.brunosong.deployment.system.deployment.service.valueobject.SourceConfigId;
import com.brunosong.deployment.system.deployment.service.valueobject.SourceId;
import com.brunosong.deployment.system.deployment.service.valueobject.SourceType;
import com.brunosong.deployment.system.deployment.service.valueobject.TransType;
import com.brunosong.deployment.system.domain.valueobject.DeploymentStatus;
import com.brunosong.deployment.system.domain.valueobject.TargetId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DeploymentTest {

    private static final Instant NOW = Instant.parse("2026-11-08T10:00:00Z");

    @Test
    @DisplayName("새 배포는 ID, PENDING 상태, 생성 시각, 청크 수를 갖춘 채로 만들어진다")
    void createBuildsCompleteDeployment() {
        Deployment deployment = Deployment.create(new SourceId("6731a2b4c5d6e7f801234567"), new SourceConfigId(1L),
                "CUR01", new TargetId(2L),
                TransType.MESSAGING, SourceType.CURRICULUM, DbType.MONGO, 4);

        assertNotNull(deployment.getId());
        assertEquals(DeploymentStatus.PENDING, deployment.getDeploymentStatus());
        assertNotNull(deployment.getCreatedAt());
        assertEquals(4, deployment.getTotalChunkSize());
        assertEquals("6731a2b4c5d6e7f801234567", deployment.getSourceId().getValue());
        assertEquals("CUR01", deployment.getCurriculumId());
        assertEquals(SourceType.CURRICULUM, deployment.getSourceType());
        assertEquals(DbType.MONGO, deployment.getDbType());
        assertTrue(deployment.getChunks().isEmpty());
    }

    @Test
    @DisplayName("청크가 하나도 없으면 배포를 만들 수 없다")
    void createRejectsZeroChunks() {
        assertThrows(DeploymentDomainException.class, () -> Deployment.create(new SourceId("6731a2b4c5d6e7f801234567"),
                new SourceConfigId(1L), "CUR01", new TargetId(2L), TransType.MESSAGING, SourceType.CURRICULUM, DbType.MONGO, 0));
    }

    @Test
    @DisplayName("접수하면 뼈대는 SENT, 과목은 WAITING 이고 배포는 IN_PROGRESS 다")
    void acceptCreatesSkeletonSentAndSubjectsWaiting() {
        Deployment deployment = Deployment.builder().build();

        deployment.accept(3, NOW);

        List<Chunk> chunks = deployment.getChunks();
        assertEquals(DeploymentStatus.IN_PROGRESS, deployment.getDeploymentStatus());
        assertEquals(4, chunks.size());
        assertEquals(0, chunks.get(0).getNo());
        assertEquals(ChunkStatus.SENT, chunks.get(0).getStatus());
        for (int no = 1; no <= 3; no++) {
            assertEquals(no, chunks.get(no).getNo());
            assertEquals(ChunkStatus.WAITING, chunks.get(no).getStatus());
        }
    }

    @Test
    @DisplayName("접수하면 보낼 청크로 뼈대 하나만 돌려준다")
    void acceptReturnsOnlySkeletonToSend() {
        Deployment deployment = Deployment.builder().build();

        List<Chunk> toSend = deployment.accept(3, NOW);

        assertEquals(1, toSend.size());
        assertEquals(0, toSend.get(0).getNo());
    }

    @Test
    @DisplayName("과목이 없어도 뼈대 하나로 접수한다")
    void acceptWithoutSubjects() {
        Deployment deployment = Deployment.builder().build();

        deployment.accept(0, NOW);

        assertEquals(1, deployment.getChunks().size());
    }

    @Test
    @DisplayName("이미 접수한 배포는 다시 접수할 수 없다")
    void cannotAcceptTwice() {
        Deployment deployment = Deployment.builder().build();
        deployment.accept(3, NOW);

        assertThrows(DeploymentDomainException.class, () -> deployment.accept(3, NOW));
    }

    @Test
    @DisplayName("과목 수가 음수면 접수할 수 없다")
    void cannotAcceptNegativeSubjectCount() {
        Deployment deployment = Deployment.builder().build();

        assertThrows(DeploymentDomainException.class, () -> deployment.accept(-1, NOW));
    }

    @Test
    @DisplayName("밖에서 청크 목록을 바꿀 수 없다")
    void chunksCannotBeModifiedFromOutside() {
        Deployment deployment = Deployment.builder().build();
        deployment.accept(3, NOW);

        assertThrows(UnsupportedOperationException.class, () -> deployment.getChunks().clear());
    }

    @Test
    @DisplayName("뼈대가 반영되면 기다리던 과목을 모두 보내고 그 목록을 돌려준다")
    void skeletonSuccessSendsWaitingSubjects() {
        Deployment deployment = accepted(3);

        List<Chunk> toSend = deployment.recordSuccess(0, NOW);

        assertEquals(3, toSend.size());
        for (int no = 1; no <= 3; no++) {
            assertEquals(ChunkStatus.SENT, deployment.getChunks().get(no).getStatus());
        }
        assertEquals(DeploymentStatus.IN_PROGRESS, deployment.getDeploymentStatus());
    }

    @Test
    @DisplayName("모든 청크가 반영되면 배포는 SUCCEEDED 다")
    void allAppliedMeansSucceeded() {
        Deployment deployment = accepted(2);
        deployment.recordSuccess(0, NOW);

        deployment.recordSuccess(1, NOW);
        deployment.recordSuccess(2, NOW);

        assertEquals(DeploymentStatus.SUCCEEDED, deployment.getDeploymentStatus());
    }

    @Test
    @DisplayName("뼈대가 실패하면 과목은 WAITING 으로 남고 배포는 FAILED 다")
    void skeletonFailureKeepsSubjectsWaiting() {
        Deployment deployment = accepted(3);

        deployment.recordFailure(0, 1, "학년을 저장하지 못했습니다", NOW);

        assertEquals(DeploymentStatus.FAILED, deployment.getDeploymentStatus());
        for (int no = 1; no <= 3; no++) {
            assertEquals(ChunkStatus.WAITING, deployment.getChunks().get(no).getStatus());
        }
    }

    @Test
    @DisplayName("과목 하나가 실패해도 응답을 기다리는 청크가 있으면 아직 IN_PROGRESS 다")
    void staysInProgressWhileAwaitingReplies() {
        Deployment deployment = accepted(3);
        deployment.recordSuccess(0, NOW);

        deployment.recordFailure(1, 1, "학기가 없습니다", NOW);

        assertEquals(DeploymentStatus.IN_PROGRESS, deployment.getDeploymentStatus());
    }

    @Test
    @DisplayName("기다릴 청크가 없고 실패가 남아 있으면 배포는 FAILED 다")
    void failedWhenNothingToAwaitAndFailureRemains() {
        Deployment deployment = accepted(3);
        deployment.recordSuccess(0, NOW);
        deployment.recordFailure(1, 1, "학기가 없습니다", NOW);

        deployment.recordSuccess(2, NOW);
        deployment.recordSuccess(3, NOW);

        assertEquals(DeploymentStatus.FAILED, deployment.getDeploymentStatus());
    }

    @Test
    @DisplayName("실패한 과목에 성공이 늦게 와서 모두 반영되면 SUCCEEDED 가 된다")
    void lateSuccessTurnsFailedIntoSucceeded() {
        Deployment deployment = accepted(2);
        deployment.recordSuccess(0, NOW);
        deployment.recordFailure(1, 1, "학기가 없습니다", NOW);
        deployment.recordSuccess(2, NOW);

        deployment.recordSuccess(1, NOW);

        assertEquals(DeploymentStatus.SUCCEEDED, deployment.getDeploymentStatus());
    }

    @Test
    @DisplayName("실패한 배포에 뼈대 성공이 늦게 와도 과목을 스스로 보내지 않는다")
    void lateSkeletonSuccessDoesNotSendSubjects() {
        Deployment deployment = accepted(3);
        deployment.recordFailure(0, 1, "학년을 저장하지 못했습니다", NOW);

        List<Chunk> toSend = deployment.recordSuccess(0, NOW);

        assertTrue(toSend.isEmpty());
        assertEquals(ChunkStatus.WAITING, deployment.getChunks().get(1).getStatus());
        assertEquals(DeploymentStatus.FAILED, deployment.getDeploymentStatus());
    }

    @Test
    @DisplayName("뼈대 성공이 중복으로 와도 과목을 다시 보내지 않는다")
    void duplicateSkeletonSuccessSendsNothing() {
        Deployment deployment = accepted(3);
        deployment.recordSuccess(0, NOW);

        List<Chunk> toSend = deployment.recordSuccess(0, NOW);

        assertTrue(toSend.isEmpty());
    }

    @Test
    @DisplayName("없는 청크 번호의 결과는 예외를 던진다")
    void unknownChunkNoThrows() {
        Deployment deployment = accepted(3);

        assertThrows(DeploymentDomainException.class, () -> deployment.recordSuccess(9, NOW));
    }

    @Test
    @DisplayName("되살린 배포도 접수한 배포처럼 결과를 처리한다")
    void restoredDeploymentHandlesResults() {
        Deployment deployment = restored();

        List<Chunk> toSend = deployment.recordSuccess(0, NOW);

        assertEquals(2, toSend.size());
        assertEquals(DeploymentStatus.IN_PROGRESS, deployment.getDeploymentStatus());
    }

    @Test
    @DisplayName("되살린 배포는 다시 접수할 수 없다")
    void restoredDeploymentCannotBeAccepted() {
        Deployment deployment = restored();

        assertThrows(DeploymentDomainException.class, () -> deployment.accept(2, NOW));
    }

    private Deployment accepted(int subjectCount) {
        Deployment deployment = Deployment.builder().build();
        deployment.accept(subjectCount, NOW);
        return deployment;
    }

    /** 뼈대를 보내 놓고 과목 둘이 기다리는 배포를 저장소에서 읽은 것처럼 만든다. */
    private Deployment restored() {
        return Deployment.builder()
                .deploymentStatus(DeploymentStatus.IN_PROGRESS)
                .chunks(List.of(
                        Chunk.restore(0, ChunkStatus.SENT, 1, NOW, null),
                        Chunk.restore(1, ChunkStatus.WAITING, 0, null, null),
                        Chunk.restore(2, ChunkStatus.WAITING, 0, null, null)
                ))
                .build();
    }
}
