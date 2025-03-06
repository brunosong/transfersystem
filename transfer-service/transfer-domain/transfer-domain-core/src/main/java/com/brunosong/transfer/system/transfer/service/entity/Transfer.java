package com.brunosong.transfer.system.transfer.service.entity;

import com.brunosong.transfer.system.domain.entity.AggregateRoot;
import com.brunosong.transfer.system.domain.valueobject.DataMigrationInfoId;
import com.brunosong.transfer.system.domain.valueobject.TransferId;
import com.brunosong.transfer.system.domain.valueobject.TransferStatus;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceId;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceType;
import com.brunosong.transfer.system.transfer.service.valueobject.TransType;

import java.time.LocalDateTime;
import java.util.UUID;

public class Transfer extends AggregateRoot<TransferId> {

    private SourceId sourceId;
    private SourceType sourceType;
    private DataMigrationInfoId dataMigrationInfoId;
    private TransferStatus transferStatus;
    private TransType transType;
    private LocalDateTime createdAt;

    private String createUserId;
    private LocalDateTime updatedAt;

    private Transfer(Builder builder) {
        setId(builder.id);
        this.sourceId = builder.sourceId;
        this.sourceType = builder.sourceType;
        this.createUserId = builder.createUserId;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
        this.dataMigrationInfoId = builder.dataMigrationInfoId;
        this.transferStatus = builder.transferStatus;
        this.transType = builder.transType;
    }

    public void updateTransferStatus(TransferStatus transferStatus) {
        this.transferStatus = transferStatus;
    }

    public void markSent() {
        if (this.transferStatus != TransferStatus.PENDING) {
            throw new IllegalStateException("Cannot mark sent from " + this.transferStatus);
        }
        this.transferStatus = TransferStatus.SENT;
        this.updatedAt = LocalDateTime.now();
    }

    public void markProcessed() {
        if (this.transferStatus != TransferStatus.SENT) {
            throw new IllegalStateException("Cannot mark processed from " + this.transferStatus);
        }
        this.transferStatus = TransferStatus.PROCESSED;
        this.updatedAt = LocalDateTime.now();
    }

    public void markSuccess() {
        if (this.transferStatus != TransferStatus.PROCESSED) {
            throw new IllegalStateException("Cannot mark success from " + this.transferStatus);
        }
        this.transferStatus = TransferStatus.SUCCESS;
        this.updatedAt = LocalDateTime.now();
    }

    public void markFailed() {
        this.transferStatus = TransferStatus.FAILED;
        this.updatedAt = LocalDateTime.now();
    }

    public void initializeTransfer() {
        setId(new TransferId(UUID.randomUUID()));
        this.transferStatus = TransferStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public DataMigrationInfoId getDataMigrationInfoId() {
        return dataMigrationInfoId;
    }

    public SourceId getSourceId() {
        return sourceId;
    }

    public SourceType getSourceType() {
        return sourceType;
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

    public TransferStatus getTransferStatus() {
        return transferStatus;
    }

    public TransType getTransType() {
        return transType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private TransferId id;
        private SourceId sourceId;
        private SourceType sourceType;
        private DataMigrationInfoId dataMigrationInfoId;
        private TransferStatus transferStatus;
        private TransType transType;
        private LocalDateTime createdAt;
        private String createUserId;
        private LocalDateTime updatedAt;

        private Builder() {
        }

        public Builder id(TransferId id) {
            this.id = id;
            return this;
        }

        public Builder sourceId(SourceId sourceId) {
            this.sourceId = sourceId;
            return this;
        }

        public Builder sourceType(SourceType sourceType) {
            this.sourceType = sourceType;
            return this;
        }

        public Builder dataMigrationInfoId(DataMigrationInfoId dataMigrationInfoId) {
            this.dataMigrationInfoId = dataMigrationInfoId;
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

        public Builder transferStatus(TransferStatus transferStatus) {
            this.transferStatus = transferStatus;
            return this;
        }

        public Builder transType(TransType transType) {
            this.transType = transType;
            return this;
        }

        public Transfer build() {
            return new Transfer(this);
        }
    }
}
