package com.brunosong.transfer.system.transfer.service;

import com.brunosong.transfer.system.transfer.service.entity.LearningMaterial;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.ports.output.message.publisher.TransferDataSendPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TransferMessagingSendHelper extends TransferSendHelper {

    private final TransferDataSendPublisher transferDataSendPublisher;

    public TransferMessagingSendHelper(TransferDataSendPublisher transferDataSendPublisher) {
        this.transferDataSendPublisher = transferDataSendPublisher;
    }

    @Override
    protected void doTransfer(Transfer transfer, LearningMaterial material) {
        transferDataSendPublisher.publish(transfer, material);
    }
}
