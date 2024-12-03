package com.brunosong.transfer.system.loadtarget.service.domain.entity;

import com.brunosong.transfer.system.domain.entity.AggregateRoot;
import com.brunosong.transfer.system.domain.valueobject.LearningMaterialId;

import java.util.List;

public class LearningMaterial extends AggregateRoot<LearningMaterialId> {

    private String title;
    private String description;
    private List<LearningMaterialMetadata> metadataList;

    private LearningMaterial(Builder builder) {
        setId(builder.id);
        this.title = title;
        this.description = description;
        this.metadataList = metadataList;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<LearningMaterialMetadata> getMetadataList() {
        return metadataList;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private LearningMaterialId id;
        private String title;
        private String description;
        private List<LearningMaterialMetadata> metadataList;

        private Builder() {}

        public Builder id(LearningMaterialId id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder metadataList(List<LearningMaterialMetadata> metadataList) {
            this.metadataList = metadataList;
            return this;
        }

        public LearningMaterial build() {
            return new LearningMaterial(this);
        }
    }
}
