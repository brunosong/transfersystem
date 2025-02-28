package com.brunosong.transfer.system.transfer.service;

import com.brunosong.transfer.system.transfer.service.dto.create.CreateTransferCommand;
import com.brunosong.transfer.system.transfer.service.entity.LearningMaterial;
import com.brunosong.transfer.system.transfer.service.exception.MaterialNotFoundException;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.LearningMaterialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class LearningMaterialCreateHelper {

    private final LearningMaterialRepository learningMaterialRepository;

    public LearningMaterial findMaterialData(String materialId) {
        LearningMaterial learningMaterial = checkAndFindLearningMaterial(materialId);
        return learningMaterial;
    }

    private LearningMaterial checkAndFindLearningMaterial(String materialId) {
        Optional<LearningMaterial> material = learningMaterialRepository.findById(materialId);

        if(material.isEmpty()) {
            log.warn("Not found LearningMaterial id : {}", materialId);
            throw new MaterialNotFoundException("Not found LearningMaterial id : " + materialId);
        }

        return material.get();
    }

}
