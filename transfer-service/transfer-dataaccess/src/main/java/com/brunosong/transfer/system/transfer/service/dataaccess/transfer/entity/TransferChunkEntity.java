package com.brunosong.transfer.system.transfer.service.dataaccess.transfer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name = "transfer_chunks")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class TransferChunkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transfer_log_id")
    private TransferLogEntity log;

    @Column(name = "chunk_offset", nullable = false)
    private int chunkOffset;

    @Column(nullable = false)
    private Integer size;

    @Column(nullable = false)
    private String status;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public void setLog(TransferLogEntity log) {
        this.log = log;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
