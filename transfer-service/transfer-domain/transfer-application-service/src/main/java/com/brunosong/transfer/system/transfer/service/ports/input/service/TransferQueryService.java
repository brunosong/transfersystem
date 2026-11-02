package com.brunosong.transfer.system.transfer.service.ports.input.service;

import com.brunosong.transfer.system.transfer.service.dto.query.PageView;
import com.brunosong.transfer.system.transfer.service.dto.query.SourceConfigView;
import com.brunosong.transfer.system.transfer.service.dto.query.TransferDetailView;
import com.brunosong.transfer.system.transfer.service.dto.query.TransferSearchCondition;
import com.brunosong.transfer.system.transfer.service.dto.query.TransferSummaryView;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 화면(콘솔)이 쓰는 조회 유스케이스. 상태를 바꾸지 않는다.
 */
public interface TransferQueryService {

    PageView<TransferSummaryView> findTransfers(TransferSearchCondition condition);

    TransferDetailView findTransfer(UUID transferId);

    List<SourceConfigView> findSourceConfigs();

    /** 상태별 전송 수. 모든 상태가 키로 들어가며 없는 상태는 0 이다. */
    Map<String, Long> countByStatus();
}
