package com.brunosong.transfer.system.transfer.service.ports.output.api;

import com.brunosong.transfer.system.transfer.service.entity.LearningMaterial;

public interface LearningMaterialApiSendService {
    void sendLearningMaterial(LearningMaterial learningMaterial);
}
