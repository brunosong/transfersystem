package com.brunosong.transfer.system.transfer.service;

import com.brunosong.transfer.system.transfer.service.dto.excution.ExecutionTransferCommand;
import com.brunosong.transfer.system.transfer.service.entity.LearningMaterial;
import com.brunosong.transfer.system.transfer.service.exception.CourseNotFoundException;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.LearningMaterialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class LearningMaterialCreateHelper {

    private final LearningMaterialRepository learningMaterialRepository;

    @Transactional
    public LearningMaterial createMaterial(ExecutionTransferCommand executionTransferCommand) {

        LearningMaterial material =
                learningMaterialRepository.findById(executionTransferCommand.getMaterialId());

        if(material == null) {
            throw new CourseNotFoundException("Not found LearningMaterial");
        }

        return material;
    }


}
