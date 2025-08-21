package com.brunosong.transfer.system.transfer.service.handler;

import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.event.TransferRequestEvent;
import com.brunosong.transfer.system.transfer.service.helper.transfer.TransferPersistHelper;
import com.brunosong.transfer.system.transfer.service.mapper.TransferDataMapper;
import com.brunosong.transfer.system.transfer.service.outbox.model.DataTransferEventPayload;
import com.brunosong.transfer.system.transfer.service.outbox.scheduler.DataTransferOutboxHelper;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceContentData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransferExecutionHandler {

//    private final TransferApiSendHelper transferApiSendHelper;
//    private final TransferMessagingSendHelper messagingSendHelper;
    private final TransferPersistHelper transferPersistHelper;
    private final DataTransferOutboxHelper dataTransferOutboxHelper;
    private final TransferDataMapper transferDataMapper;


    @Transactional
    public Transfer sendData(Transfer transfer, SourceContentData sourceContentData) {


        // 1. 데이터 전송 전 로그 저장
        List<TransferRequestEvent> transferRequestEvents = transferPersistHelper.persistTransferLog(transfer,sourceContentData);

        for (TransferRequestEvent transferRequestEvent : transferRequestEvents) {

            // 데이터 전송 이벤트 페이로드 생성
            DataTransferEventPayload dataTransferEventPayload =
                    transferDataMapper.transferRequestEventToDataTransferEventPayload(transferRequestEvent);

            // 데이터 전송 아웃박스 메시지 저장
            dataTransferOutboxHelper.saveDataTransferOutboxMessage(dataTransferEventPayload,
                    UUID.randomUUID());

        }

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


    public void updateTransferSendResult(Transfer transfer) {
        transferPersistHelper.updateTransferSendResult(transfer);
    }

}
