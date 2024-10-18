package com.brunosong.transfer.system.transfer.service;

import com.brunosong.transfer.system.transfer.service.dto.excution.ExcutionTransferCommand;
import com.brunosong.transfer.system.transfer.service.entity.LearningMaterial;
import com.brunosong.transfer.system.transfer.service.exception.CourseNotFoundException;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.LearningMaterialRepository;
import com.brunosong.transfer.system.transfer.service.ports.output.service.LearningMaterialSendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TransferSendServiceHelper extends TransferSendHelper {

    private final LearningMaterialSendService learningMaterialSendService;

    public TransferSendServiceHelper(LearningMaterialRepository learningMaterialRepository,
                                     LearningMaterialSendService learningMaterialSendService) {
        super(learningMaterialRepository);
        this.learningMaterialSendService = learningMaterialSendService;
    }

    public void transferAction(ExcutionTransferCommand excutionTransferCommand) {

        LearningMaterial learningMaterial = getLearningMaterial(excutionTransferCommand.getMaterialId());

        if(learningMaterial == null) {
            throw new CourseNotFoundException("Not found LearningMaterial");
        }

        learningMaterialSendService.sendLearningMaterial(learningMaterial);
    }

}
