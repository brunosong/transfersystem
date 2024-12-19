package com.brunosong.transfer.system.transfer.service.entity;

import com.brunosong.transfer.system.domain.entity.AggregateRoot;
import com.brunosong.transfer.system.domain.valueobject.DataMigrationInfoId;
import com.brunosong.transfer.system.domain.valueobject.LearningMaterialId;
import com.brunosong.transfer.system.domain.valueobject.TransferId;

import java.time.LocalDateTime;
import java.util.UUID;

public class Transfer extends AggregateRoot<TransferId> {
    private LearningMaterialId learningMaterialId;
    private DataMigrationInfoId dataMigrationInfoId;
    private LocalDateTime createdAt;
    private String createAdminId;
    private LocalDateTime updatedAt;

    private Transfer(Builder builder) {
        setId(builder.id);
        this.createAdminId = builder.createAdminId;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
        this.learningMaterialId = builder.learningMaterialId;
        this.dataMigrationInfoId = builder.dataMigrationInfoId;
    }

    public LearningMaterialId getLearningMaterialId() {
        return learningMaterialId;
    }

    public DataMigrationInfoId getDataMigrationInfoId() {
        return dataMigrationInfoId;
    }

    public String getCreateAdminId() {
        return createAdminId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void initializeTransfer() {
        setId(new TransferId(UUID.randomUUID()));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private TransferId id;
        private String createAdminId;
        private LearningMaterialId learningMaterialId;
        private DataMigrationInfoId dataMigrationInfoId;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        private Builder() {
        }

        public Builder id(TransferId id) {
            this.id = id;
            return this;
        }

        public Builder materialId(LearningMaterialId learningMaterialId) {
            this.learningMaterialId = learningMaterialId;
            return this;
        }

        public Builder dataMigrationInfoId(DataMigrationInfoId dataMigrationInfoId) {
            this.dataMigrationInfoId = dataMigrationInfoId;
            return this;
        }

        public Builder createAdminId(String createAdminId) {
            this.createAdminId = createAdminId;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Transfer build() {
            return new Transfer(this);
        }
    }
}
