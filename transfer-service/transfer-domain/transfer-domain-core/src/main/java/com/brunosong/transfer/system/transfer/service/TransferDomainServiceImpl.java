package com.brunosong.transfer.system.transfer.service;


import com.brunosong.transfer.system.domain.DomainConstants;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.event.TransferEvent;
import com.brunosong.transfer.system.transfer.service.event.TransferRequestEvent;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceContentData;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Slf4j
public class TransferDomainServiceImpl implements TransferDomainService {

    @Override
    public TransferRequestEvent validateAndInitiateTransfer(Transfer transfer, SourceContentData sourceContentData) {
        transfer.validateInitialTransfer();
        transfer.initializeTransfer(sourceContentData);

        return new TransferRequestEvent(transfer, sourceContentData, ZonedDateTime.now(ZoneId.of(DomainConstants.UTC)));
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
