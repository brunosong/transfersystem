package com.brunosong.deployment.system.deployment.service.handler;

import com.brunosong.deployment.system.deployment.service.dto.create.ApplyResultMessage;
import com.brunosong.deployment.system.deployment.service.entity.Chunk;
import com.brunosong.deployment.system.deployment.service.entity.Deployment;
import com.brunosong.deployment.system.deployment.service.mapper.DeploymentDataMapper;
import com.brunosong.deployment.system.deployment.service.outbox.model.DeploymentEventPayload;
import com.brunosong.deployment.system.deployment.service.outbox.scheduler.DeploymentOutboxHelper;
import com.brunosong.deployment.system.deployment.service.ports.output.repository.ChunkPayload;
import com.brunosong.deployment.system.deployment.service.ports.output.repository.ChunkPayloadRepository;
import com.brunosong.deployment.system.deployment.service.ports.output.repository.DeploymentRepository;
import com.brunosong.deployment.system.deployment.service.valueobject.*;
import com.brunosong.deployment.system.domain.valueobject.DeploymentStatus;
import com.brunosong.deployment.system.domain.valueobject.TargetId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DeploymentResultHandlerTest {

    private final DeploymentRepository deploymentRepository = mock(DeploymentRepository.class);
    private final ChunkPayloadRepository chunkPayloadRepository = mock(ChunkPayloadRepository.class);
    private final DeploymentOutboxHelper outboxHelper = mock(DeploymentOutboxHelper.class);
    // 매퍼는 진짜를 쓴다. 과목 페이로드가 실제로 어떻게 만들어지는지까지 본다
    private final DeploymentResultHandler handler = new DeploymentResultHandler(
            deploymentRepository, chunkPayloadRepository, outboxHelper, new DeploymentDataMapper());

    private final List<ChunkContent> chunks = ChunkContent.split(curriculum());   // 뼈대 1 + 과목 2

    @Test
    @DisplayName("뼈대가 반영되면 기다리던 과목들을 번호 순서대로 아웃박스에 넣는다")
    void skeletonSuccessPutsSubjectsIntoOutbox() {
        Deployment deployment = accepted();
        when(chunkPayloadRepository.findAll(deployment.getId().getValue(), List.of(1, 2)))
                .thenReturn(List.of(new ChunkPayload(1, chunks.get(1)), new ChunkPayload(2, chunks.get(2))));

        handler.applyResult(success(deployment, 0));

        ArgumentCaptor<DeploymentEventPayload> payloads = ArgumentCaptor.forClass(DeploymentEventPayload.class);
        verify(outboxHelper, times(2)).saveDeploymentOutboxMessage(payloads.capture());
        assertThat(payloads.getAllValues()).extracting(DeploymentEventPayload::getChunkOffset).containsExactly(1, 2);
        assertThat(deployment.getChunks()).extracting(Chunk::getStatus)
                .containsExactly(ChunkStatus.APPLIED, ChunkStatus.SENT, ChunkStatus.SENT);
        verify(deploymentRepository).save(deployment);
        verify(deploymentRepository).saveChunk(any());   // 옛 표에도 적는다
    }

    @Test
    @DisplayName("과목이 반영되면 아웃박스에 아무것도 넣지 않고, 마지막 과목까지 반영되면 배포가 완료된다")
    void subjectSuccessSendsNothingAndLastOneCompletes() {
        Deployment deployment = accepted();
        deployment.recordSuccess(0, Instant.now());   // 뼈대는 이미 반영됐다

        handler.applyResult(success(deployment, 1));
        assertThat(deployment.getDeploymentStatus()).isEqualTo(DeploymentStatus.IN_PROGRESS);

        handler.applyResult(success(deployment, 2));
        assertThat(deployment.getDeploymentStatus()).isEqualTo(DeploymentStatus.SUCCEEDED);

        verify(outboxHelper, never()).saveDeploymentOutboxMessage(any());
    }

    @Test
    @DisplayName("실패 결과는 그 청크를 실패로 기록하고, 아웃박스에 아무것도 넣지 않는다")
    void failureRecordsChunkFailed() {
        Deployment deployment = accepted();

        handler.applyResult(ApplyResultMessage.builder()
                .deploymentId(deployment.getId()).deploymentStatus(DeploymentStatus.FAILED)
                .chunkOffset(0).attempt(1).message("학기 표가 없습니다").build());

        assertThat(deployment.getChunks().get(0).getStatus()).isEqualTo(ChunkStatus.FAILED);
        verify(outboxHelper, never()).saveDeploymentOutboxMessage(any());
    }

    /** 접수까지 끝난 배포. 뼈대 SENT, 과목 둘 WAITING. 저장소가 이 배포를 돌려준다 */
    private Deployment accepted() {
        Deployment deployment = Deployment.create(new SourceId("CUR01"), new SourceConfigId(1L), "CUR01",
                new TargetId(1L), TransType.MESSAGING, SourceType.CURRICULUM, DbType.MONGO, chunks.size());
        deployment.accept(chunks.size() - 1, Instant.now());
        when(deploymentRepository.findById(deployment.getId().getValue())).thenReturn(Optional.of(deployment));
        return deployment;
    }

    private static ApplyResultMessage success(Deployment deployment, int chunkNo) {
        return ApplyResultMessage.builder()
                .deploymentId(deployment.getId()).deploymentStatus(DeploymentStatus.SUCCESS)
                .chunkOffset(chunkNo).attempt(1).message("SUCCESS").build();
    }

    /** 학년 하나, 학기 하나, 과목 둘 */
    private static Curriculum curriculum() {
        Curriculum.Subject math = new Curriculum.Subject("S1", "수학", null, List.of());
        Curriculum.Subject korean = new Curriculum.Subject("S2", "국어", null, List.of());
        Curriculum.Semester semester = new Curriculum.Semester("SEM1", "1학기", null, List.of(math, korean));
        Curriculum.Grade grade = new Curriculum.Grade("G1", "1학년", null, List.of(semester));
        return new Curriculum("CUR01", "초등 과정", null, List.of(grade));
    }
}