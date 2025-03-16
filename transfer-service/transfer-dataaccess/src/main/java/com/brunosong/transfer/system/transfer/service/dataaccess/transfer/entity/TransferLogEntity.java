package com.brunosong.transfer.system.transfer.service.dataaccess.transfer.entity;

import com.brunosong.transfer.system.domain.valueobject.TransferStatus;
import com.brunosong.transfer.system.transfer.service.valueobject.DbType;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceType;
import com.brunosong.transfer.system.transfer.service.valueobject.TransType;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "transfer_log")
@Entity
@ToString
@EntityListeners(AuditingEntityListener.class)
public class TransferLogEntity {

    @Id
    @Column(name = "transfer_id")
    private UUID id;

    @Column(name = "source_id", nullable = false)
    private String sourceId;

    @Column(name = "sourceconfig_id", nullable = false)
    private Long sourceconfigId;

    @Enumerated(EnumType.STRING)
    private SourceType sourceType;

    @Column(name = "data_migration_info_id", nullable = false)
    private UUID dataMigrationInfoId;

    @Enumerated(EnumType.STRING)
    private DbType dbType;

    @Enumerated(EnumType.STRING)
    @Column(name = "trans_type", nullable = false)
    private TransType transType;

    @Enumerated(EnumType.STRING)
    @Column(name = "transfer_status", nullable = false)
    private TransferStatus transferStatus;

    @Column(name = "create_admin_id", nullable = false)
    private String createAdminId;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "error_message")
    private String errorMessage;

    @OneToMany(mappedBy = "log", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransferChunkEntity> chunks = new ArrayList<>();

    public void updateResult(TransferStatus status, String errorMessage) {
        this.transferStatus = status;
        this.errorMessage = errorMessage;
    }

    public void addChunk(TransferChunkEntity chunk) {
        chunks.add(chunk);
        chunk.setLog(this);
    }

    public void updateTransferStatus(TransferStatus transferStatus) {
        this.transferStatus = transferStatus;
    }

}
