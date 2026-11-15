package com.brunosong.deployment.system.deployment.service.handler;

import com.brunosong.deployment.system.deployment.service.entity.Deployment;
import com.brunosong.deployment.system.deployment.service.ports.output.repository.DeploymentRepository;
import com.brunosong.deployment.system.deployment.service.valueobject.*;
import com.brunosong.deployment.system.domain.valueobject.DeploymentStatus;
import com.brunosong.deployment.system.domain.valueobject.TargetId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class DeploymentDeadlineHandlerTest {

    private static final Instant SENT_AT = Instant.parse("2026-11-15T10:00:00Z");
    private static final Duration DEADLINE = Duration.ofMinutes(10);

    private final DeploymentRepository deploymentRepository = mock(DeploymentRepository.class);
    private final DeploymentDeadlineHandler handler = new DeploymentDeadlineHandler(deploymentRepository);

    @Test
    @DisplayName("기한이 지난 배포를 읽어 응답 없음으로 바꾸고 저장한다")
    void expiresAndSaves() {
        Deployment deployment = Deployment.create(new SourceId("CUR01"), new SourceConfigId(1L), "CUR01",
                new TargetId(1L), TransType.MESSAGING, SourceType.CURRICULUM, DbType.MONGO, 1);
        deployment.accept(0, SENT_AT);   // 뼈대 하나만
        when(deploymentRepository.findById(deployment.getId().getValue())).thenReturn(Optional.of(deployment));

        handler.expireOverdue(deployment.getId().getValue(), SENT_AT.plus(DEADLINE).plusSeconds(1), DEADLINE);

        assertThat(deployment.getChunks().get(0).getStatus()).isEqualTo(ChunkStatus.FAILED);
        assertThat(deployment.getDeploymentStatus()).isEqualTo(DeploymentStatus.FAILED);
        verify(deploymentRepository).save(deployment);
    }
}