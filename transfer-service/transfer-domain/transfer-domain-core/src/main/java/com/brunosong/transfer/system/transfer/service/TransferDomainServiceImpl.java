package com.brunosong.transfer.system.transfer.service;


import com.brunosong.transfer.system.domain.DomainConstants;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.event.TransferEvent;
import com.brunosong.transfer.system.transfer.service.event.TransferRequestEvent;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceContentData;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TransferDomainServiceImpl implements TransferDomainService {

    @Override
    public List<TransferRequestEvent> validateAndInitiateTransfer(Transfer transfer, SourceContentData sourceContentData) {
        transfer.validateInitialTransfer();
        transfer.initializeTransfer(sourceContentData);

        // 도메인 객채는 같고 데이터와 청크 순서만 다른 TransferRequestEvent 객체를 생성하여 반환
        List<TransferRequestEvent> transferRequestEventList = new ArrayList<>();
        List<byte[]> chunkDataList = sourceContentData.getChunkDataList();
        for (int chunkOffset = 0; chunkOffset < chunkDataList.size(); chunkOffset++) {
            // 청크 데이터 유효성 검사
            transferRequestEventList.add(
                new TransferRequestEvent(transfer, ZonedDateTime.now(ZoneId.of(DomainConstants.UTC)), chunkOffset, chunkDataList.get(chunkOffset))
            );
        }
        return transferRequestEventList;
    }

    @Override
    public void markSent(Transfer transfer) {
        log.info("Transfer with id: {} is markSent", transfer.getId().getValue());
        transfer.markSent();
    }

    @Override
    public void markProcessed(Transfer transfer) {
        log.info("Transfer with id: {} is markProcessed", transfer.getId().getValue());
        transfer.markProcessed();
    }

    @Override
    public void markSuccess(Transfer transfer) {
        log.info("Transfer with id: {} is markSuccess", transfer.getId().getValue());
        transfer.markSuccess();
    }

    @Override
    public void markFailed(Transfer transfer) {
        log.info("Transfer with id: {} is markFailed", transfer.getId().getValue());
        transfer.markFailed();
    }
}
