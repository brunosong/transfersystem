package com.brunosong.transfer.system.deployment.service.dataaccess.deployment.entity;

import com.brunosong.transfer.system.domain.valueobject.DeploymentStatus;
import com.brunosong.transfer.system.deployment.service.valueobject.DbType;
import com.brunosong.transfer.system.deployment.service.valueobject.SourceType;
import com.brunosong.transfer.system.deployment.service.valueobject.TransType;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "deployment")
@Entity
@ToString
@EntityListeners(AuditingEntityListener.class)
public class DeploymentEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Column(name = "source_id", nullable = false)
    private String sourceId;

    @Column(name = "sourceconfig_id", nullable = false)
    private Long sourceconfigId;

    @Enumerated(EnumType.STRING)
    //@JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private SourceType sourceType;

    @Column(name = "datamigration_id", nullable = false)
    private Long dataMigrationInfoId;

    @Enumerated(EnumType.STRING)
    //@JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "db_type", columnDefinition = "db_type")
    private DbType dbType;

    @Enumerated(EnumType.STRING)
    @Column(name = "trans_type", nullable = false)
    private TransType transType;

    @Enumerated(EnumType.STRING)
    @Column(name = "deployment_status", nullable = false)
    private DeploymentStatus deploymentStatus;

    @Column(name = "create_admin_id", nullable = false)
    private String createAdminId;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "result_message")
    private String resultMessage;

    @Column(name = "total_chunk_size")
    private int totalChunkSize;

    @OneToMany(mappedBy = "log", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeploymentChunkEntity> chunks = new ArrayList<>();

    public void updateResult(DeploymentStatus status, String resultMessage) {
        this.deploymentStatus = status;
        this.resultMessage = resultMessage;
    }

    public void updateSendResult(DeploymentStatus status, int totalChunkSize) {
        this.deploymentStatus = status;
        this.totalChunkSize = totalChunkSize;
    }

    public void addChunk(DeploymentChunkEntity chunk) {
        chunks.add(chunk);
        chunk.setLog(this);
    }

    public void updateDeploymentStatus(DeploymentStatus deploymentStatus) {
        this.deploymentStatus = deploymentStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeploymentEntity that = (DeploymentEntity) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean isNew() {
        return createdAt == null;
    }

}
