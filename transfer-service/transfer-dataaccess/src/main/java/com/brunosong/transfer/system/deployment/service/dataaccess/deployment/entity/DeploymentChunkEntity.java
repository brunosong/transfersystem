package com.brunosong.transfer.system.deployment.service.dataaccess.deployment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name = "deployment_chunks",
       uniqueConstraints = @UniqueConstraint(name = "uk_deployment_chunk",
                                             columnNames = {"deployment_id", "chunk_offset"}))
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class DeploymentChunkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deployment_id")
    private DeploymentEntity log;

    @Column(name = "chunk_offset", nullable = false)
    private int chunkOffset;

    @Column(nullable = false)
    private Integer size;

    @Column(nullable = false)
    private String status;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public void setLog(DeploymentEntity log) {
        this.log = log;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
