package com.brunosong.transfer.system.transfer.service;


import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceContentData;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TransferDomainServiceImpl implements TransferDomainService {
    @Override
    public void validateAndInitiateTransfer(Transfer transfer, SourceContentData sourceContentData) {
        transfer.validateInitialTransfer();
        transfer.initializeTransfer(sourceContentData);
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
