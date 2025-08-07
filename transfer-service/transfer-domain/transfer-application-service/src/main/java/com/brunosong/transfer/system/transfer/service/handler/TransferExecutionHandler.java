package com.brunosong.transfer.system.transfer.service.handler;

import com.brunosong.transfer.system.transfer.service.dto.create.TransferLogResult;
import com.brunosong.transfer.system.transfer.service.helper.transfer.TransferApiSendHelper;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.helper.transfer.TransferMessagingSendHelper;
import com.brunosong.transfer.system.transfer.service.helper.transfer.TransferPersistHelper;
import com.brunosong.transfer.system.transfer.service.helper.transfer.curriculum.CurriculumSourceConverter;
import com.brunosong.transfer.system.transfer.service.mapper.TransferDataMapper;
import com.brunosong.transfer.system.transfer.service.valueobject.TransType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransferExecutionHandler {

    private final TransferApiSendHelper transferApiSendHelper;
    private final TransferMessagingSendHelper messagingSendHelper;
    private final TransferPersistHelper transferPersistHelper;


    @Transactional
    public Transfer sendData(Transfer transfer) {

        // 1. 데이터 전송 전 로그 저장
        // 상태를 저장하고 이벤트를 객체를 만들어야 한다.
        TransferLogResult transferLogResult = persistTransferLog(transfer);
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

    public TransferLogResult persistTransferLog(Transfer transfer) {

        Transfer savedTransfer = transferPersistHelper.persistTransferLog(transfer);

        String logMessage = String.format("Transfer log created with type: %s, status: %s",
                savedTransfer.getTransType().getDescription(),
                savedTransfer.getTransferStatus().getDescription());
        return new TransferLogResult(savedTransfer.getId(), logMessage);
    }

    public void updateTransferSendResult(Transfer transfer) {
        transferPersistHelper.updateTransferSendResult(transfer);
    }

}
