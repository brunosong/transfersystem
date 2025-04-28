package com.brunosong.transfer.system.transfer.service.handler;

import com.brunosong.transfer.system.transfer.service.dto.create.TransferLogResult;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.helper.transfer.TransferPersistHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransferLogHandler {

    private final TransferPersistHelper transferPersistHelper;

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
