package com.brunosong.transfer.system.transfer.service.dataaccess.transfer.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "transfer_chunks")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferChunkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operation_id", nullable = false)
    private TransferOperationEntity operation;

    @Column(nullable = false)
    private Long chunkOffset;

    @Column(nullable = false)
    private Integer size;

    @Column(nullable = false)
    private String status;

    public void setOperation(TransferOperationEntity operation) {
        this.operation = operation;
    }
}
