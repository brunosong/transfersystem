package com.brunosong.deployment.system.deployment.service.dataaccess.deployment.entity;

import com.brunosong.deployment.system.deployment.service.valueobject.ChunkStatus;
import com.brunosong.deployment.system.deployment.service.valueobject.FailureKind;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@Table(name = "deployment_chunk")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChunkEntity {

    @EmbeddedId
    private ChunkEntityId id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChunkStatus status;

    @Column(nullable = false)
    private int attempt;

    @Column(name = "sent_at")
    private Instant sentAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "failure_kind")
    private FailureKind failureKind;

    @Column(name = "failure_message")
    private String failureMessage;

    @Column(name = "failed_at")
    private Instant failedAt;
}
