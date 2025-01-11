package com.brunosong.transfer.system.transfer.service.infrastructure.mapper;

import com.brunosong.transfer.system.transfer.service.entity.LearningMaterial;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.infrastructure.adapter.LearningMaterialApiModel;
import org.springframework.stereotype.Component;

@Component
public class TransferApiDataMapper {

    public LearningMaterialApiModel toLearningMaterialApiModel(Transfer transfer, LearningMaterial learningMaterial) {
        return LearningMaterialApiModel.builder()
                .learningMaterial(learningMaterial)
                .dataMigrationInfoId(transfer.getDataMigrationInfoId().getValue())
                .build();

    }

}
