package com.brunosong.transfer.system.transfer.service;

import com.brunosong.transfer.system.domain.valueobject.TransferStatus;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;

import java.time.LocalDateTime;

public interface TransferDomainService {
    void validateAndInitiateTransfer(Transfer transfer);

    void markSent(Transfer transfer);

    void markProcessed(Transfer transfer);

    void markSuccess(Transfer transfer);

    void markFailed(Transfer transfer);

}
