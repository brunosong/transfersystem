package com.brunosong.deployment.system.deployment.service.dataaccess.deployment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ChunkEntityId implements Serializable {

    @Column(name = "deployment_id")
    private UUID deploymentId;

    @Column(name = "chunk_no")
    private int chunkNo;

}
