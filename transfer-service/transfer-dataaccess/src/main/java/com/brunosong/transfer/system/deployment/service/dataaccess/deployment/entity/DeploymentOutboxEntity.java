package com.brunosong.transfer.system.deployment.service.dataaccess.deployment.entity;

import com.brunosong.transfer.system.outbox.OutboxStatus;
import com.brunosong.transfer.system.saga.SagaStatus;
import com.brunosong.transfer.system.deployment.service.valueobject.TransType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "deployment_outbox")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeploymentOutboxEntity {

    @Id
    private UUID id;

    @Column(name = "saga_id", nullable = false)
    private UUID sagaId;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    // 발행에 성공한 뒤에 찍히는 값이라 저장 시점에는 비어 있다
    @Column(name = "processed_at")
    private ZonedDateTime processedAt;

    @Column(name = "type", nullable = false)
    private String type;

    // 어느 경로로 내보낼지. JSON 을 열어보지 않고 고르려고 컬럼으로 뺐다
    @Enumerated(EnumType.STRING)
    @Column(name = "trans_type", nullable = false)
    private TransType transType;

    @Column(name = "payload", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "saga_status", nullable = false)
    private SagaStatus sagaStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "outbox_status", nullable = false)
    private OutboxStatus outboxStatus;

    @Column(name = "version", nullable = false)
    private int version;
}
