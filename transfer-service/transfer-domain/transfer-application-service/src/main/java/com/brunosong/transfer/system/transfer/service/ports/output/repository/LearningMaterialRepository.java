package com.brunosong.transfer.system.transfer.service.ports.output.repository;

import com.brunosong.transfer.system.transfer.service.entity.LearningMaterial;

import java.util.List;

public interface LearningMaterialRepository {
    List<LearningMaterial> findById(Long materialId);
}
