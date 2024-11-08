package com.brunosong.transfer.system.transfer.service.ports.output.message.publisher;

import com.brunosong.transfer.system.transfer.service.entity.LearningMaterial;

public interface LearningMaterialRequestPublisher {
    void publish(LearningMaterial learningMaterial);
}
