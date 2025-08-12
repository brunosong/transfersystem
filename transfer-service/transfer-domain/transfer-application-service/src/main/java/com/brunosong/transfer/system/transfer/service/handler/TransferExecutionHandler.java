package com.brunosong.transfer.system.transfer.service.handler;

import com.brunosong.transfer.system.domain.valueobject.TransferId;
import com.brunosong.transfer.system.transfer.service.TransferDomainService;
import com.brunosong.transfer.system.transfer.service.event.TransferRequestEvent;
import com.brunosong.transfer.system.transfer.service.exception.TransferDomainException;
import com.brunosong.transfer.system.transfer.service.helper.transfer.TransferApiSendHelper;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.helper.transfer.TransferMessagingSendHelper;
import com.brunosong.transfer.system.transfer.service.helper.transfer.TransferPersistHelper;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceContentData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransferExecutionHandler {

//    private final TransferApiSendHelper transferApiSendHelper;
//    private final TransferMessagingSendHelper messagingSendHelper;
    private final TransferPersistHelper transferPersistHelper;
    private final TransferDomainService transferDomainService;


    @Transactional
    public Transfer sendData(Transfer transfer, SourceContentData sourceContentData) {


        // 1. 소스 데이터 유효성 검사
        TransferRequestEvent transferRequestEvent = transferDomainService.validateAndInitiateTransfer(transfer, sourceContentData);

        // 2. 데이터 전송 전 로그 저장
        TransferId transferId = persistTransferLog(transfer);
        transferRequestEvent.setTransferId(transferId);
//
//
//
//        // 2. 전송할 데이터 설정 정보 찾기
//        if (transfer.getTransType() == TransType.API) {
//            transferApiSendHelper.transferAction(transfer);
//            log.info("API transfer completed: {}", transfer.getId());
//        } else if (transfer.getTransType() == TransType.MESSAGING) {
//            messagingSendHelper.transferAction(transfer);
//            log.info("Messaging transfer completed: {}", transfer.getId());
//        } else {
//            transfer.markFailed();
//            log.warn("Unknown transfer type for: {}", transfer.getId());
//        }

        // 4. 데이터 전송 결과 업데이트
        updateTransferSendResult(transfer);

        return transfer;
    }

    public TransferId persistTransferLog(Transfer transfer) {

        Transfer transferResult = transferPersistHelper.persistTransferLog(transfer);

        if (transferResult == null) {
            log.error("Could not save transferLog!");
            throw new TransferDomainException("Could not save transferLog!");
        }

        log.info("Transfer log created with type: {}, status: {}",
                transferResult.getTransType().getDescription(),
                transferResult.getTransferStatus().getDescription());

        return transferResult.getId();
    }

    public void updateTransferSendResult(Transfer transfer) {
        transferPersistHelper.updateTransferSendResult(transfer);
    }

}
