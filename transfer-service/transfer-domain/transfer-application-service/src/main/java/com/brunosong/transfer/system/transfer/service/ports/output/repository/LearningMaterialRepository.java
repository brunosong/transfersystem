package com.brunosong.transfer.system.transfer.service.ports.output.repository;

import com.brunosong.transfer.system.transfer.service.entity.LearningMaterial;

import java.util.Optional;

public interface LearningMaterialRepository {
    Optional<LearningMaterial> findById(String materialId);
}
