package com.brunosong.transfer.system.transfer.service;

import com.brunosong.transfer.system.transfer.service.entity.LearningMaterial;
import com.brunosong.transfer.system.transfer.service.ports.output.api.LearningMaterialApiSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TransferApiSendHelper extends TransferSendHelper {

    private final LearningMaterialApiSender learningMaterialApiSender;

    public TransferApiSendHelper(LearningMaterialApiSender learningMaterialApiSender) {
        this.learningMaterialApiSender = learningMaterialApiSender;
    }

    @Override
    public void transferAction(LearningMaterial learningMaterial) {
        learningMaterialApiSender.sendLearningMaterial(learningMaterial);
    }

}
