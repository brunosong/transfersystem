package com.brunosong.deployment.system.deployment.service.handler;

import com.brunosong.deployment.system.deployment.service.entity.Chunk;
import com.brunosong.deployment.system.deployment.service.entity.Deployment;
import com.brunosong.deployment.system.deployment.service.helper.deployment.DeploymentPersistHelper;
import com.brunosong.deployment.system.deployment.service.mapper.DeploymentDataMapper;
import com.brunosong.deployment.system.deployment.service.outbox.model.DeploymentEventPayload;
import com.brunosong.deployment.system.deployment.service.outbox.scheduler.DeploymentOutboxHelper;
import com.brunosong.deployment.system.deployment.service.valueobject.*;
import com.brunosong.deployment.system.domain.valueobject.DeploymentStatus;
import com.brunosong.deployment.system.domain.valueobject.TargetId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class DeploymentExecutionHandlerTest {

    private final DeploymentPersistHelper persistHelper = mock(DeploymentPersistHelper.class);
    private final DeploymentOutboxHelper outboxHelper = mock(DeploymentOutboxHelper.class);
    // 매퍼는 진짜를 쓴다. 페이로드가 실제로 어떻게 만들어지는지까지 본다
    private final DeploymentExecutionHandler handler =
            new DeploymentExecutionHandler(persistHelper, outboxHelper, new DeploymentDataMapper());


    @Test
    @DisplayName("접수하면 아웃박스에는 뼈대 하나만 들어가고, 과목은 기다린다")
    void acceptPutsOnlySkeletonIntoOutbox() {
        List<ChunkContent> chunks = ChunkContent.split(curriculum());   // 뼈대 1 + 과목 2
        Deployment deployment = Deployment.create(new SourceId("6731a2b4c5d6e7f801234567"), new SourceConfigId(1L),
                "CUR01", new TargetId(1L), TransType.MESSAGING, SourceType.CURRICULUM, DbType.MONGO, chunks.size());

        handler.acceptDeployment(deployment, chunks);

        // 아웃박스: 뼈대 한 번만
        ArgumentCaptor<DeploymentEventPayload> payload = ArgumentCaptor.forClass(DeploymentEventPayload.class);
        verify(outboxHelper, times(1)).saveDeploymentOutboxMessage(payload.capture());

        assertThat(payload.getValue().getChunkOffset()).isZero();
        assertThat(payload.getValue().getContent()).isInstanceOf(ChunkContent.Skeleton.class);

        // 내용은 전부 저장한다. 과목은 나중에 이 내용으로 보낸다
        verify(persistHelper).persistDeployment(deployment, chunks);

        // 도메인 상태: 배포 진행 중, 뼈대 SENT, 과목 WAITING
        assertThat(deployment.getDeploymentStatus()).isEqualTo(DeploymentStatus.IN_PROGRESS);
        assertThat(deployment.getChunks()).extracting(Chunk::getStatus)
                .containsExactly(ChunkStatus.SENT, ChunkStatus.WAITING, ChunkStatus.WAITING);
    }

    /** 학년 하나, 학기 하나, 과목 둘. 뼈대 1개 + 과목 2개로 나뉜다 */
    private static Curriculum curriculum() {
        Curriculum.Subject math = new Curriculum.Subject("S1", "수학", null, List.of());
        Curriculum.Subject korean = new Curriculum.Subject("S2", "국어", null, List.of());
        Curriculum.Semester semester = new Curriculum.Semester("SEM1", "1학기", null, List.of(math, korean));
        Curriculum.Grade grade = new Curriculum.Grade("G1", "1학년", null, List.of(semester));
        return new Curriculum("CUR01", "초등 과정", null, List.of(grade));
    }
}