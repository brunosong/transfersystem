package com.brunosong.transfer.system.transfer.service;

import com.brunosong.transfer.system.transfer.service.dto.excution.ExcutionTransferCommand;
import com.brunosong.transfer.system.transfer.service.entity.LearningMaterial;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.ChapterRepository;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.CourseRepository;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.LearningMaterialRepository;

import java.util.List;

public abstract class TransferSendHelper {

    private final LearningMaterialRepository learningMaterialRepository;

    public TransferSendHelper(LearningMaterialRepository learningMaterialRepository) {
        this.learningMaterialRepository = learningMaterialRepository;
    }

    public LearningMaterial getLearningMaterial(Long materialId) {
        return learningMaterialRepository.findById(materialId);
    }

    abstract void courseTransferProcess(ExcutionTransferCommand excutionTransferCommand);
    abstract void chapterTransferProcess(ExcutionTransferCommand excutionTransferCommand);
}
