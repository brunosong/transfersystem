package com.brunosong.transfer.system.deployment.service.entity;

import com.brunosong.transfer.system.domain.entity.AggregateRoot;
import com.brunosong.transfer.system.domain.valueobject.DataMigrationId;
import com.brunosong.transfer.system.domain.valueobject.DeploymentId;
import com.brunosong.transfer.system.domain.valueobject.DeploymentStatus;
import com.brunosong.transfer.system.deployment.service.exception.DeploymentDomainException;
import com.brunosong.transfer.system.deployment.service.valueobject.*;

import java.time.LocalDateTime;
import java.util.UUID;

public class Deployment extends AggregateRoot<DeploymentId> {

    private SourceId sourceId;
    private SourceContentData sourceContentData;
    private DataMigrationId dataMigrationId;
    private DeploymentStatus deploymentStatus;
    private TransType transType;
    private SourceConfigId sourceConfigId;
    private LocalDateTime createdAt;
    private String createUserId;
    private LocalDateTime updatedAt;
    private String resultMessage;
    private int totalChunkSize;

    private Deployment(Builder builder) {
        setId(builder.id);
        this.sourceId = builder.sourceId;
        this.createUserId = builder.createUserId;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
        this.dataMigrationId = builder.dataMigrationId;
        this.deploymentStatus = builder.deploymentStatus;
        this.transType = builder.transType;
        this.sourceConfigId = builder.sourceConfigId;
        this.sourceContentData = builder.sourceContentData;
        this.resultMessage = builder.resultMessage;
        this.totalChunkSize = builder.totalChunkSize;
    }

    public void updateDeploymentStatus(DeploymentStatus deploymentStatus) {
        this.deploymentStatus = deploymentStatus;
    }

    public void markSent() {
        if (this.deploymentStatus != DeploymentStatus.PENDING) {
            throw new IllegalStateException("Cannot mark sent from " + this.deploymentStatus);
        }
        this.deploymentStatus = DeploymentStatus.SENT;
        this.updatedAt = LocalDateTime.now();
    }

    public void markProcessed() {
        if (this.deploymentStatus != DeploymentStatus.SENT) {
            throw new IllegalStateException("Cannot mark processed from " + this.deploymentStatus);
        }
        this.deploymentStatus = DeploymentStatus.PROCESSED;
        this.updatedAt = LocalDateTime.now();
    }

    public void markSuccess() {
        if (this.deploymentStatus != DeploymentStatus.PROCESSED) {
            throw new IllegalStateException("Cannot mark success from " + this.deploymentStatus);
        }
        this.deploymentStatus = DeploymentStatus.SUCCESS;
        this.updatedAt = LocalDateTime.now();
    }

    public void markFailed() {
        this.deploymentStatus = DeploymentStatus.FAILED;
        this.updatedAt = LocalDateTime.now();
    }

    public void initializeDeployment(SourceContentData sourceContentData) {
        setId(new DeploymentId(UUID.randomUUID()));
        this.deploymentStatus = DeploymentStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.sourceContentData = sourceContentData;
    }

    public void validateInitialDeployment() {
        if (deploymentStatus != null || getCreatedAt() != null || getSourceContentData() != null) {
            throw new DeploymentDomainException("Deployment is not in correct state for initialization!");
        }
    }

    public DataMigrationId getDataMigrationId() {
        return dataMigrationId;
    }

    public SourceId getSourceId() {
        return sourceId;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public DeploymentStatus getDeploymentStatus() {
        return deploymentStatus;
    }

    public TransType getTransType() {
        return transType;
    }

    public SourceConfigId getSourceConfigId() {
        return sourceConfigId;
    }

    public SourceContentData getSourceContentData() {
        return sourceContentData;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public int getTotalChunkSize() {
        return totalChunkSize;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private DeploymentId id;
        private SourceId sourceId;
        private SourceContentData sourceContentData;
        private DataMigrationId dataMigrationId;
        private DeploymentStatus deploymentStatus;
        private TransType transType;
        private SourceConfigId sourceConfigId;
        private LocalDateTime createdAt;
        private String createUserId;
        private LocalDateTime updatedAt;
        private String resultMessage;
        private int totalChunkSize;

        private Builder() {
        }

        public Builder id(DeploymentId id) {
            this.id = id;
            return this;
        }

        public Builder sourceId(SourceId sourceId) {
            this.sourceId = sourceId;
            return this;
        }

        public Builder sourceContentData(SourceContentData sourceContentData) {
            this.sourceContentData = sourceContentData;
            return this;
        }

        public Builder dataMigrationInfoId(DataMigrationId dataMigrationId) {
            this.dataMigrationId = dataMigrationId;
            return this;
        }

        public Builder createUserId(String createUserId) {
            this.createUserId = createUserId;
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

        public Builder deploymentStatus(DeploymentStatus deploymentStatus) {
            this.deploymentStatus = deploymentStatus;
            return this;
        }

        public Builder transType(TransType transType) {
            this.transType = transType;
            return this;
        }

        public Builder sourceConfigId(SourceConfigId sourceConfigId) {
            this.sourceConfigId = sourceConfigId;
            return this;
        }

        public Builder resultMessage(String resultMessage) {
            this.resultMessage = resultMessage;
            return this;
        }

        public Builder totalChunkSize(int totalChunkSize) {
            this.totalChunkSize = totalChunkSize;
            return this;
        }

        public Deployment build() {
            return new Deployment(this);
        }
    }
}
