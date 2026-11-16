package com.brunosong.deployment.system.deployment.service;

import com.brunosong.deployment.system.domain.valueobject.DeploymentStatus;
import com.brunosong.deployment.system.deployment.service.dto.query.ChunkRecord;
import com.brunosong.deployment.system.deployment.service.dto.query.DeploymentDetailView;
import com.brunosong.deployment.system.deployment.service.dto.query.DeploymentRecord;
import com.brunosong.deployment.system.deployment.service.dto.query.TargetView;
import com.brunosong.deployment.system.deployment.service.exception.DeploymentNotFoundException;
import com.brunosong.deployment.system.deployment.service.ports.output.repository.DeploymentQueryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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

    @Test
    @DisplayName("대상 목록은 임시 목록의 대상을 ID 순으로, 메시지 계약과 같은 이름으로 돌려준다")
    void targetsInIdOrder() {
        DeploymentQueryServiceImpl service = new DeploymentQueryServiceImpl(mock(DeploymentQueryRepository.class));

        assertThat(service.findTargets())
                .extracting(TargetView::targetId, TargetView::system, TargetView::environment)
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple(1L, "ONLINE_CAMPUS", "PROD"),
                        org.assertj.core.groups.Tuple.tuple(2L, "BOOTCAMP", "PROD"),
                        org.assertj.core.groups.Tuple.tuple(3L, "ONLINE_CAMPUS", "DEV"),
                        org.assertj.core.groups.Tuple.tuple(4L, "BOOTCAMP", "DEV"));
    }

    private final DeploymentQueryRepository repository = mock(DeploymentQueryRepository.class);
    private final DeploymentQueryServiceImpl service = new DeploymentQueryServiceImpl(repository);

    @Test
    @DisplayName("상세는 대상과 청크들을 바로 담고, 청크마다 운영자가 알아볼 이름을 붙인다")
    void detailHasTargetAndChunks() {
        UUID id = UUID.randomUUID();
        when(repository.findDeployment(id)).thenReturn(Optional.of(record(id, DeploymentStatus.IN_PROGRESS, 3, 1, 0)));
        Instant processedAt = Instant.now();
        // 준비: 뼈대는 재전송 끝에 반영됐고(시도 2), 과목 2는 대상이 거절했다(시도 1)
        when(repository.findChunks(id)).thenReturn(List.of(
                new ChunkRecord(0, "APPLIED", processedAt, null, 2),
                new ChunkRecord(2, "FAILED", processedAt, "Duplicate entry", 1)));

        DeploymentDetailView detail = service.findDeployment(id);

        assertThat(detail.status()).isEqualTo("IN_PROGRESS");
        assertThat(detail.targetId()).isEqualTo(1L);
        assertThat(detail.progress().pending()).isEqualTo(2);
        // 확인: 청크마다 이름이 붙고, 실패 사유가 그대로 오고, 재전송 횟수는 시도 번호 - 1 이다
        assertThat(detail.chunks()).containsExactly(
                new DeploymentDetailView.ChunkView(0, "뼈대", "APPLIED", processedAt, null, 1),
                new DeploymentDetailView.ChunkView(2, "과목 2", "FAILED", processedAt, "Duplicate entry", 0));
    }

    @Test
    @DisplayName("상태별 수는 모든 상태를 키로 담고 없는 상태는 0 이다")
    void countByStatusFillsZeros() {
        when(repository.countByStatus()).thenReturn(Map.of(DeploymentStatus.FAILED, 2L));

        assertThat(service.countByStatus())
                .containsOnlyKeys("IN_PROGRESS", "SUCCEEDED", "FAILED")
                .containsEntry("FAILED", 2L)
                .containsEntry("SUCCEEDED", 0L);
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
