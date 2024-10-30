package com.brunosong.transfer.system.transfer.service.dataaccess.material.adapter;

import com.brunosong.transfer.system.dataaccess.meterials.entity.LearningMaterialViewEntity;
import com.brunosong.transfer.system.dataaccess.meterials.repository.LearningMaterialMongoRepository;
import com.brunosong.transfer.system.transfer.service.dataaccess.material.mapper.LearningMaterialDataAccessMapper;
import com.brunosong.transfer.system.transfer.service.entity.LearningMaterial;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.LearningMaterialRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class LearningMaterialRepositoryImpl implements LearningMaterialRepository {

    private final LearningMaterialMongoRepository learningMaterialMongoRepository;
    private final LearningMaterialDataAccessMapper learningMaterialDataAccessMapper;

    public LearningMaterialRepositoryImpl(LearningMaterialMongoRepository learningMaterialMongoRepository,
                                          LearningMaterialDataAccessMapper learningMaterialDataAccessMapper) {
        this.learningMaterialMongoRepository = learningMaterialMongoRepository;
        this.learningMaterialDataAccessMapper = learningMaterialDataAccessMapper;
    }

    @Override
    public Optional<LearningMaterial> findById(String materialId) {
        return learningMaterialMongoRepository.findById(materialId)
                .map(learningMaterialDataAccessMapper::learningMaterialEntityToLearningMaterial);
    }
}
