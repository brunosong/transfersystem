package com.brunosong.transfer.system.transfer.service;

import com.brunosong.transfer.system.transfer.service.entity.LearningMaterial;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.ports.output.message.publisher.TransferDataSendMessagePublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TransferMessagingSendHelper extends TransferSendHelper {

    private final TransferDataSendMessagePublisher transferDataSendMessagePublisher;

    public TransferMessagingSendHelper(TransferDataSendMessagePublisher transferDataSendMessagePublisher) {
        this.transferDataSendMessagePublisher = transferDataSendMessagePublisher;
    }

    @Override
    protected void doTransfer(Transfer transfer, LearningMaterial material) {
        transferDataSendMessagePublisher.publish(transfer, material);
    }
}
