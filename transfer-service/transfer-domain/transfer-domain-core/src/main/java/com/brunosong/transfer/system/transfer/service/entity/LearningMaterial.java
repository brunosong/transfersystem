package com.brunosong.transfer.system.transfer.service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LearningMaterial {

    private String id;
    private String title;
    private String description;
    private List<LearningMaterialMetadata> metadataList;

}
