package com.brunosong.transfer.system.transfer.service.dataaccess.material.adapter;

import com.brunosong.transfer.system.dataaccess.meterials.repository.LearningMaterialJpaRepository;
import com.brunosong.transfer.system.transfer.service.dataaccess.material.mapper.LearningMaterialDataAccessMapper;
import com.brunosong.transfer.system.transfer.service.entity.LearningMaterial;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.LearningMaterialRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LearningMaterialRepositoryImpl implements LearningMaterialRepository {

    private final LearningMaterialJpaRepository learningMaterialJpaRepository;
    private final LearningMaterialDataAccessMapper learningMaterialDataAccessMapper;

    public LearningMaterialRepositoryImpl(LearningMaterialJpaRepository learningMaterialJpaRepository,
                                          LearningMaterialDataAccessMapper learningMaterialDataAccessMapper) {
        this.learningMaterialJpaRepository = learningMaterialJpaRepository;
        this.learningMaterialDataAccessMapper = learningMaterialDataAccessMapper;
    }

    @Override
    public LearningMaterial findById(Long materialId) {
        return learningMaterialJpaRepository.findById(materialId).map(
                learningMaterialDataAccessMapper::learningMaterialEntityToLearningMaterial).get();
    }
}
