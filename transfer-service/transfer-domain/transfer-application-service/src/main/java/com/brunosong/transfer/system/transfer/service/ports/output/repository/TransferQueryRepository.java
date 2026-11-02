package com.brunosong.transfer.system.transfer.service.ports.output.repository;

import com.brunosong.transfer.system.domain.valueobject.TransferStatus;
import com.brunosong.transfer.system.transfer.service.dto.query.ChunkRecord;
import com.brunosong.transfer.system.transfer.service.dto.query.PageView;
import com.brunosong.transfer.system.transfer.service.dto.query.SourceConfigView;
import com.brunosong.transfer.system.transfer.service.dto.query.TransferRecord;
import com.brunosong.transfer.system.transfer.service.dto.query.TransferSearchCondition;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * 조회 전용 출력 포트. 도메인 애그리거트를 거치지 않고 읽기 모델을 바로 돌려준다.
 */
public interface TransferQueryRepository {

    PageView<TransferRecord> findTransfers(TransferSearchCondition condition);

    Optional<TransferRecord> findTransfer(UUID transferId);

    List<ChunkRecord> findChunks(UUID transferId);

    List<SourceConfigView> findSourceConfigs();

    Map<TransferStatus, Long> countByStatus();
}
