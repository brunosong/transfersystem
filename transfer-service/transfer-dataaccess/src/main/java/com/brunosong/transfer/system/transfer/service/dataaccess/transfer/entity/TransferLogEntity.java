package com.brunosong.transfer.system.transfer.service.dataaccess.transfer.entity;

import com.brunosong.transfer.system.domain.valueobject.TransferStatus;
import com.brunosong.transfer.system.transfer.service.valueobject.TransType;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
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
    private UUID id;

    private String learningMaterialId;

    private UUID dataMigrationInfoId;

    @Enumerated(EnumType.STRING)
    private TransType transType;

    @Enumerated(EnumType.STRING)
    private TransferStatus transferStatus;

    private String createAdminId;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

}
