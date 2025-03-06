package com.brunosong.transfer.system.transfer.service.infrastructure.mapper;

import com.brunosong.transfer.system.transfer.service.valueobject.LearningMaterial;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.infrastructure.adapter.LearningMaterialApiModel;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceContentData;
import org.springframework.stereotype.Component;

@Component
public class TransferApiDataMapper {

    public LearningMaterialApiModel toLearningMaterialApiModel(Transfer transfer, SourceContentData sourceContentData) {
        return LearningMaterialApiModel.builder()
                //.learningMaterial(learningMaterial)
                .dataMigrationInfoId(transfer.getDataMigrationInfoId().getValue())
                .build();

    }

}
