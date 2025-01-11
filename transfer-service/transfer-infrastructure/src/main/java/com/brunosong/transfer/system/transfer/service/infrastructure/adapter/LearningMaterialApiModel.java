package com.brunosong.transfer.system.transfer.service.infrastructure.adapter;

import com.brunosong.transfer.system.transfer.service.entity.LearningMaterial;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LearningMaterialApiModel {
    private UUID dataMigrationInfoId;
    private LearningMaterial learningMaterial;
}
