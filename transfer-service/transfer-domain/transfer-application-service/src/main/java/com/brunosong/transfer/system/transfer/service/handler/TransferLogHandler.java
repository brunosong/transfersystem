package com.brunosong.transfer.system.transfer.service.handler;

import com.brunosong.transfer.system.transfer.service.dto.create.TransferLogResult;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.domain.valueobject.TransferStatus;
import com.brunosong.transfer.system.transfer.service.helper.transfer.TransferCreateHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TransferLogHandler {

    private final TransferCreateHelper transferCreateHelper;

    @Transactional
    public TransferLogResult persistTransferLog(Transfer transfer) {
        Transfer savedTransfer = transferCreateHelper.persistTransferLog(transfer);
        if (savedTransfer.getTransferStatus() == TransferStatus.PROCESSED) {
            savedTransfer.markSuccess();
        }
        String logMessage = String.format("Transfer log created with type: %s, status: %s",
                savedTransfer.getTransType().getDescription(),
                savedTransfer.getTransferStatus().getDescription());
        return new TransferLogResult(savedTransfer.getId(), logMessage);
    }

}
