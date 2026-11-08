package com.brunosong.deployment.system.deployment.service.entity;

import com.brunosong.deployment.system.deployment.service.exception.DeploymentDomainException;
import com.brunosong.deployment.system.deployment.service.valueobject.ChunkStatus;
import com.brunosong.deployment.system.domain.valueobject.DeploymentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DeploymentTest {

    private static final Instant NOW = Instant.parse("2026-11-08T10:00:00Z");

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
}
