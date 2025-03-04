package com.brunosong.transfer.system.transfer.service.dataaccess.transfer.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "transfer_operations")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferOperationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_info", nullable = false)
    private String sourceInfo;

    @Column(name = "destination_info", nullable = false)
    private String destinationInfo;

    @Column(nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "operation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransferChunkEntity> chunks = new ArrayList<>();

    // Getters, Setters
    public void addChunk(TransferChunkEntity chunk) {
        chunks.add(chunk);
        chunk.setOperation(this);
    }
}
