package com.brunosong.transfer.system.transfer.service.helper.transfer;

import com.brunosong.transfer.system.domain.valueobject.TransferId;
import com.brunosong.transfer.system.transfer.service.TransferDomainService;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.event.TransferRequestEvent;
import com.brunosong.transfer.system.transfer.service.exception.TransferDomainException;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.TransferLogRepository;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceContentData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransferPersistHelper {

    private final TransferLogRepository transferLogRepository;
    private final TransferDomainService transferDomainService;

    @Transactional
    public List<TransferRequestEvent> persistTransferLog(Transfer transfer, SourceContentData sourceContentData) {

        // 1. 데이터 유효성 검사 및 이벤트 객체 생성
        List<TransferRequestEvent> transferRequestEvents = transferDomainService.validateAndInitiateTransfer(transfer, sourceContentData);

        // 2. 전송 로그 저장
        Transfer transferResult = saveTransferLog(transfer);

        // 3. 이벤트 객체에 전송 ID 설정
        TransferId transferId = transferResult.getId();
        transferRequestEvents.forEach(event -> event.getTransfer().setId(transferId));

        return transferRequestEvents;

    }

    @Transactional
    public void updateTransferSendResult(Transfer transfer) {
        transferLogRepository.updateTransferSendResult(transfer);
        log.info("UpdateTransferSendResult transferId is {}", transfer.getId());
    }

    private Transfer saveTransferLog(Transfer transfer) {

        Transfer transferResult = transferLogRepository.save(transfer);

        if (transferResult == null) {
            log.error("Could not save transferLog!");
            throw new TransferDomainException("Could not save transferLog!");
        }
        return transferResult;
    }
}
