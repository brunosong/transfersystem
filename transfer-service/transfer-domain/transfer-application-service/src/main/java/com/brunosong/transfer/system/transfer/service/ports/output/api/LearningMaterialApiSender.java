package com.brunosong.transfer.system.transfer.service.ports.output.api;

import com.brunosong.transfer.system.transfer.service.entity.LearningMaterial;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;

public interface LearningMaterialApiSender {
    void sendLearningMaterial(Transfer transfer, LearningMaterial learningMaterial);
}
