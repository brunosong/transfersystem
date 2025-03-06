package com.brunosong.transfer.system.transfer.service.valueobject;

import java.util.List;

public class LearningMaterial implements SourceData {

    private Long learningMaterialId;
    private String title;
    private String description;
    private List<LearningMaterialMetadata> metadataList;

    @Override
    public byte[] getData() {
        return new byte[0];
    }

    @Override
    public String getId() {
        return null;
    }
}
