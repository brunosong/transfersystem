package com.brunosong.transfer.system.transfer.service;

import com.brunosong.transfer.system.domain.valueobject.TransferStatus;
import com.brunosong.transfer.system.transfer.service.dto.query.ChunkRecord;
import com.brunosong.transfer.system.transfer.service.dto.query.TransferDetailView;
import com.brunosong.transfer.system.transfer.service.dto.query.TransferProgressView;
import com.brunosong.transfer.system.transfer.service.dto.query.TransferRecord;
import com.brunosong.transfer.system.transfer.service.dto.query.TransferStepView;
import com.brunosong.transfer.system.transfer.service.exception.TransferLogNotFoundException;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.TransferQueryRepository;
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

class TransferQueryServiceImplTest {

    private final TransferQueryRepository repository = mock(TransferQueryRepository.class);
    private final TransferQueryServiceImpl service = new TransferQueryServiceImpl(repository);

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
    void stepStatus(TransferStatus status, int total, long succeeded, long failed, String expected) {
        assertThat(TransferQueryServiceImpl.stepStatus(status, new TransferProgressView(total, succeeded, failed)))
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("상세는 사가 모양으로 단계 하나(APPLY)와 그 청크들을 담는다")
    void detailHasApplyStep() {
        UUID id = UUID.randomUUID();
        when(repository.findTransfer(id)).thenReturn(Optional.of(record(id, TransferStatus.SENT, 3, 1, 0)));
        List<ChunkRecord> chunks = List.of(new ChunkRecord(1, "SUCCESS", Instant.now()));
        when(repository.findChunks(id)).thenReturn(chunks);

        TransferDetailView detail = service.findTransfer(id);

        assertThat(detail.status()).isEqualTo("SENT");
        assertThat(detail.progress().pending()).isEqualTo(2);
        assertThat(detail.steps()).singleElement().satisfies(step -> {
            TransferStepView s = (TransferStepView) step;
            assertThat(s.action()).isEqualTo("APPLY");
            assertThat(s.targetId()).isEqualTo(1L);
            assertThat(s.status()).isEqualTo("PROCESSING");
            assertThat(s.chunks()).isEqualTo(chunks);
        });
    }

    @Test
    @DisplayName("상태별 수는 모든 상태를 키로 담고 없는 상태는 0 이다")
    void countByStatusFillsZeros() {
        when(repository.countByStatus()).thenReturn(Map.of(TransferStatus.FAILED, 2L));

        assertThat(service.countByStatus())
                .containsOnlyKeys("PENDING", "SENT", "PROCESSED", "SUCCESS", "FAILED")
                .containsEntry("FAILED", 2L)
                .containsEntry("SUCCESS", 0L);
    }

    @Test
    @DisplayName("없는 전송을 찾으면 TransferLogNotFoundException")
    void notFound() {
        UUID id = UUID.randomUUID();
        when(repository.findTransfer(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findTransfer(id)).isInstanceOf(TransferLogNotFoundException.class);
    }

    private static TransferRecord record(UUID id, TransferStatus status, int total, long succeeded, long failed) {
        LocalDateTime now = LocalDateTime.now();
        return new TransferRecord(id, "curriculum-1", "CURRICULUM", 4L, 1L, status,
                total, succeeded, failed, "brunosong", now, now, null);
    }
}
