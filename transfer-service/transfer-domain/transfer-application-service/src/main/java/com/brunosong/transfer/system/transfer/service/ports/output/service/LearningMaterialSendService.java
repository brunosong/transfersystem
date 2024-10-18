package com.brunosong.transfer.system.transfer.service.ports.output.service;

import com.brunosong.transfer.system.transfer.service.entity.LearningMaterial;

public interface LearningMaterialSendService {
    void sendLearningMaterial(LearningMaterial learningMaterial);
}
