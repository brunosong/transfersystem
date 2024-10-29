package com.brunosong.transfer.system.transfer.service;

import com.brunosong.transfer.system.transfer.service.entity.LearningMaterial;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.LearningMaterialRepository;
import com.brunosong.transfer.system.transfer.service.ports.output.api.LearningMaterialApiSendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TransferSendServiceHelper extends TransferSendHelper {

    private final LearningMaterialApiSendService learningMaterialSendService;

    public TransferSendServiceHelper(LearningMaterialRepository learningMaterialRepository,
                                     LearningMaterialApiSendService learningMaterialApiSendService) {
        super(learningMaterialRepository);
        this.learningMaterialSendService = learningMaterialApiSendService;
    }

    public void transferAction(LearningMaterial learningMaterial) {
        learningMaterialSendService.sendLearningMaterial(learningMaterial);
    }

}
