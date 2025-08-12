package com.brunosong.transfer.system.transfer.service;

import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.event.TransferEvent;
import com.brunosong.transfer.system.transfer.service.event.TransferRequestEvent;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceContentData;

import java.time.LocalDateTime;

public interface TransferDomainService {

    TransferRequestEvent validateAndInitiateTransfer(Transfer transfer, SourceContentData sourceContentData);

    void markSent(Transfer transfer);

    void markProcessed(Transfer transfer);

    void markSuccess(Transfer transfer);

    void markFailed(Transfer transfer);

}
