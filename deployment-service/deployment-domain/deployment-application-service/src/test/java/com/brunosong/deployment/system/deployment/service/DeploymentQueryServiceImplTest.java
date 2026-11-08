package com.brunosong.deployment.system.deployment.service;

import com.brunosong.deployment.system.domain.valueobject.DeploymentStatus;
import com.brunosong.deployment.system.deployment.service.dto.query.ChunkRecord;
import com.brunosong.deployment.system.deployment.service.dto.query.DeploymentDetailView;
import com.brunosong.deployment.system.deployment.service.dto.query.DeploymentProgressView;
import com.brunosong.deployment.system.deployment.service.dto.query.DeploymentRecord;
import com.brunosong.deployment.system.deployment.service.dto.query.DeploymentStepView;
import com.brunosong.deployment.system.deployment.service.exception.DeploymentNotFoundException;
import com.brunosong.deployment.system.deployment.service.ports.output.repository.DeploymentQueryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DeploymentQueryServiceImplTest {

    private final DeploymentQueryRepository repository = mock(DeploymentQueryRepository.class);
    private final DeploymentQueryServiceImpl service = new DeploymentQueryServiceImpl(repository);

    @ParameterizedTest(name = "{0}, total={1} succeeded={2} failed={3} -> {4}")
    @CsvSource({
            "PENDING,   0, 0, 0, PENDING",
            "PENDING,   5, 2, 0, PROCESSING",
            "SENT,      5, 0, 0, PROCESSING",
            "PROCESSED, 5, 5, 0, SUCCEEDED",
            "SUCCESS,   0, 0, 0, SUCCEEDED",
            "PROCESSED, 5, 3, 1, FAILED",
            "FAILED,    5, 0, 0, FAILED",
    })
    @DisplayName("단계 상태는 청크 결과를 먼저 보고 정한다")
    void stepStatus(DeploymentStatus status, int total, long succeeded, long failed, String expected) {
        assertThat(DeploymentQueryServiceImpl.stepStatus(status, new DeploymentProgressView(total, succeeded, failed)))
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("상세는 사가 모양으로 단계 하나(APPLY)와 그 청크들을 담는다")
    void detailHasApplyStep() {
        UUID id = UUID.randomUUID();
        when(repository.findDeployment(id)).thenReturn(Optional.of(record(id, DeploymentStatus.SENT, 3, 1, 0)));
        List<ChunkRecord> chunks = List.of(new ChunkRecord(1, "SUCCESS", Instant.now()));
        when(repository.findChunks(id)).thenReturn(chunks);

        DeploymentDetailView detail = service.findDeployment(id);

        assertThat(detail.status()).isEqualTo("SENT");
        assertThat(detail.progress().pending()).isEqualTo(2);
        assertThat(detail.steps()).singleElement().satisfies(step -> {
            DeploymentStepView s = (DeploymentStepView) step;
            assertThat(s.action()).isEqualTo("APPLY");
            assertThat(s.targetId()).isEqualTo(1L);
            assertThat(s.status()).isEqualTo("PROCESSING");
            assertThat(s.chunks()).isEqualTo(chunks);
        });
    }

    @Test
    @DisplayName("상태별 수는 모든 상태를 키로 담고 없는 상태는 0 이다")
    void countByStatusFillsZeros() {
        when(repository.countByStatus()).thenReturn(Map.of(DeploymentStatus.FAILED, 2L));

        assertThat(service.countByStatus())
                .containsOnlyKeys("PENDING", "SENT", "PROCESSED", "SUCCESS", "IN_PROGRESS", "SUCCEEDED", "FAILED")
                .containsEntry("FAILED", 2L)
                .containsEntry("SUCCESS", 0L);
    }

    @Test
    @DisplayName("없는 배포를 찾으면 DeploymentNotFoundException")
    void notFound() {
        UUID id = UUID.randomUUID();
        when(repository.findDeployment(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findDeployment(id)).isInstanceOf(DeploymentNotFoundException.class);
    }

    private static DeploymentRecord record(UUID id, DeploymentStatus status, int total, long succeeded, long failed) {
        LocalDateTime now = LocalDateTime.now();
        return new DeploymentRecord(id, "curriculum-1", "CURRICULUM", 4L, 1L, status,
                total, succeeded, failed, "brunosong", now, now, null);
    }
}
