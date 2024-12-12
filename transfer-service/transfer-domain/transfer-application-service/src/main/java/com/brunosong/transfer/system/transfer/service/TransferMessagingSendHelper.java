package com.brunosong.transfer.system.transfer.service;

import com.brunosong.transfer.system.transfer.service.entity.LearningMaterial;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.ports.output.message.publisher.LearningMaterialRequestPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TransferMessagingSendHelper extends TransferSendHelper {

    private final LearningMaterialRequestPublisher learningMaterialRequestPublisher;

    public TransferMessagingSendHelper(LearningMaterialRequestPublisher learningMaterialRequestPublisher) {
        this.learningMaterialRequestPublisher = learningMaterialRequestPublisher;
    }

    public void transferAction(Transfer transfer, LearningMaterial learningMaterial) {
        learningMaterialRequestPublisher.publish(transfer, learningMaterial);
    }

}
