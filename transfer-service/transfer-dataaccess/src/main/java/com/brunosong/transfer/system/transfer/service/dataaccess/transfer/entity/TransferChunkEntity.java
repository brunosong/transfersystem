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
@AllArgsConstructor
@NoArgsConstructor
public class TransferChunkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "log_id", nullable = false)
    private TransferLogEntity log;

    @Column(name = "chunk_offset", nullable = false)
    private Long chunkOffset;

    @Column(nullable = false)
    private Integer size;

    @Column(nullable = false)
    private String status;

    public void setLog(TransferLogEntity log) {
        this.log = log;
    }

}
