package com.brunosong.transfer.system.transfer.service;

import com.brunosong.transfer.system.transfer.service.dto.excution.ExcutionTransferCommand;
import com.brunosong.transfer.system.transfer.service.dto.excution.ExcutionTransferResponse;
import com.brunosong.transfer.system.transfer.service.valueobject.TransType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TransferExcutionHandler {

    private final TransferSendServiceHelper transferSendServiceHelper;
    private final TransferSendMessageHelper transferSendMessageHelper;

    @Transactional
    public ExcutionTransferResponse createMaterial(ExcutionTransferCommand excutionTransferCommand) {

        if (excutionTransferCommand.getTransType() == TransType.DB) {
            transferSendServiceHelper.transferAction(excutionTransferCommand);
        } else {
            transferSendMessageHelper.transferAction(excutionTransferCommand);
        }

        return null;
    }

}
