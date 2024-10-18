package com.brunosong.transfer.system.transfer.service;

import com.brunosong.transfer.system.transfer.service.dto.excution.ExcutionTransferCommand;
import com.brunosong.transfer.system.transfer.service.entity.LearningMaterial;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.LearningMaterialRepository;

public abstract class TransferSendHelper {

    private final LearningMaterialRepository learningMaterialRepository;

    public TransferSendHelper(LearningMaterialRepository learningMaterialRepository) {
        this.learningMaterialRepository = learningMaterialRepository;
    }

    public LearningMaterial getLearningMaterial(String materialId) {
        return learningMaterialRepository.findById(materialId);
    }

}
