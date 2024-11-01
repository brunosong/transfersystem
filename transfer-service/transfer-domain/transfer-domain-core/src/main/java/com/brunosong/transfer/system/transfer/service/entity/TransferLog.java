package com.brunosong.transfer.system.transfer.service.entity;

import com.brunosong.transfer.system.domain.entity.AggregateRoot;
import com.brunosong.transfer.system.domain.valueobject.LearningMaterialId;
import com.brunosong.transfer.system.domain.valueobject.TransferLogId;

import java.time.LocalDateTime;
import java.util.UUID;

public class TransferLog extends AggregateRoot<TransferLogId> {

    private TransferLogId id;
    private String createAdminId;
    private LearningMaterialId learningMaterialId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private TransferLog(Builder builder) {
        setId(builder.id);
        this.createAdminId = builder.createAdminId;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
        this.learningMaterialId = builder.learningMaterialId;
    }

    public TransferLogId getId() {
        return id;
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

    public void initializeTransferLog() {
        setId(new TransferLogId(UUID.randomUUID()));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private TransferLogId id;
        private String createAdminId;
        private LearningMaterialId learningMaterialId;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        private Builder() {
        }

        public Builder id(TransferLogId id) {
            this.id = id;
            return this;
        }

        public Builder materialId(LearningMaterialId learningMaterialId) {
            this.learningMaterialId = learningMaterialId;
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

        public TransferLog build() {
            return new TransferLog(this);
        }
    }
}
