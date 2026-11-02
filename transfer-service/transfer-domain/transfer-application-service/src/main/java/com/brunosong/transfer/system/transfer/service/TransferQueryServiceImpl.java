package com.brunosong.transfer.system.transfer.service;

import com.brunosong.transfer.system.domain.valueobject.TransferStatus;
import com.brunosong.transfer.system.transfer.service.dto.query.ChunkRecord;
import com.brunosong.transfer.system.transfer.service.dto.query.PageView;
import com.brunosong.transfer.system.transfer.service.dto.query.SourceConfigView;
import com.brunosong.transfer.system.transfer.service.dto.query.TransferDetailView;
import com.brunosong.transfer.system.transfer.service.dto.query.TransferProgressView;
import com.brunosong.transfer.system.transfer.service.dto.query.TransferRecord;
import com.brunosong.transfer.system.transfer.service.dto.query.TransferSearchCondition;
import com.brunosong.transfer.system.transfer.service.dto.query.TransferStepView;
import com.brunosong.transfer.system.transfer.service.dto.query.TransferSummaryView;
import com.brunosong.transfer.system.transfer.service.exception.TransferLogNotFoundException;
import com.brunosong.transfer.system.transfer.service.ports.input.service.TransferQueryService;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.TransferQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransferQueryServiceImpl implements TransferQueryService {

    static final String ACTION_APPLY = "APPLY";

    private final TransferQueryRepository transferQueryRepository;

    @Override
    public PageView<TransferSummaryView> findTransfers(TransferSearchCondition condition) {
        return transferQueryRepository.findTransfers(condition).map(record -> new TransferSummaryView(
                record.transferId(),
                record.sourceId(),
                record.sourceType(),
                record.status().name(),
                progressOf(record),
                record.createdBy(),
                record.createdAt(),
                record.updatedAt()));
    }

    @Override
    public TransferDetailView findTransfer(UUID transferId) {
        TransferRecord record = transferQueryRepository.findTransfer(transferId)
                .orElseThrow(() -> new TransferLogNotFoundException("Transfer not found: " + transferId));
        List<ChunkRecord> chunks = transferQueryRepository.findChunks(transferId);
        TransferProgressView progress = progressOf(record);

        // 사가 이전이라 단계는 "대상 하나에 반영" 하나뿐이다. 사가가 들어오면
        // 사가 상태 테이블에서 단계 목록을 읽어 그대로 내려 주면 된다.
        TransferStepView applyStep = new TransferStepView(
                1,
                ACTION_APPLY,
                record.dataMigrationInfoId(),
                null,
                stepStatus(record.status(), progress),
                progress,
                chunks);

        return new TransferDetailView(
                record.transferId(),
                record.sourceId(),
                record.sourceType(),
                record.sourceConfigId(),
                record.status().name(),
                record.resultMessage(),
                progress,
                record.createdBy(),
                record.createdAt(),
                record.updatedAt(),
                List.of(applyStep));
    }

    @Override
    public List<SourceConfigView> findSourceConfigs() {
        return transferQueryRepository.findSourceConfigs();
    }

    @Override
    public Map<String, Long> countByStatus() {
        Map<TransferStatus, Long> counts = transferQueryRepository.countByStatus();
        Map<String, Long> result = new LinkedHashMap<>();
        for (TransferStatus status : TransferStatus.values()) {
            result.put(status.name(), counts.getOrDefault(status, 0L));
        }
        return result;
    }

    static TransferProgressView progressOf(TransferRecord record) {
        return new TransferProgressView(record.totalChunks(), record.succeededChunks(), record.failedChunks());
    }

    /**
     * 단계 상태는 청크 결과를 먼저 본다. 실패한 청크가 하나라도 있으면 실패, 모든 청크가
     * 성공했으면 성공, 아직 아무 결과도 없고 전송 전이면 대기, 그 밖에는 진행 중이다.
     */
    static String stepStatus(TransferStatus transferStatus, TransferProgressView progress) {
        if (progress.failed() > 0 || transferStatus == TransferStatus.FAILED) {
            return "FAILED";
        }
        if (transferStatus == TransferStatus.SUCCESS
                || (progress.total() > 0 && progress.succeeded() >= progress.total())) {
            return "SUCCEEDED";
        }
        if (transferStatus == TransferStatus.PENDING && progress.succeeded() == 0) {
            return "PENDING";
        }
        return "PROCESSING";
    }
}
