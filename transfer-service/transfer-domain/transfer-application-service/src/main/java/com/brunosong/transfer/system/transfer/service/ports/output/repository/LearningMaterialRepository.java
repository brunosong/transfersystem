package com.brunosong.transfer.system.transfer.service.ports.output.repository;

import com.brunosong.transfer.system.transfer.service.entity.LearningMaterial;

public interface LearningMaterialRepository {
    LearningMaterial findById(String materialId);
}
