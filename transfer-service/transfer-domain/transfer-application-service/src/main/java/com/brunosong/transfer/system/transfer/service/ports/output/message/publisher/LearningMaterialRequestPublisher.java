package com.brunosong.transfer.system.transfer.service.ports.output.message.publisher;

import com.brunosong.transfer.system.transfer.service.entity.LearningMaterial;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;

public interface LearningMaterialRequestPublisher {
    void publish(Transfer transfer, LearningMaterial learningMaterial);
}
