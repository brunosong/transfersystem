package com.brunosong.transfer.system.transfer.service;

import com.brunosong.transfer.system.transfer.service.entity.LearningMaterial;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.ports.output.api.TransferDataApiSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TransferApiSendHelper extends TransferSendHelper {

    private final TransferDataApiSender transferDataApiSender;

    public TransferApiSendHelper(TransferDataApiSender transferDataApiSender) {
        this.transferDataApiSender = transferDataApiSender;
    }

    @Override
    protected void doTransfer(Transfer transfer, LearningMaterial material) {
        transferDataApiSender.sendLearningMaterial(transfer, material);
    }
}
