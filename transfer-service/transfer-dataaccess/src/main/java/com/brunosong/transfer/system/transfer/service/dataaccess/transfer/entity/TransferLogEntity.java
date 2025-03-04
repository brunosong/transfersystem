package com.brunosong.transfer.system.transfer.service.dataaccess.transfer.entity;

import com.brunosong.transfer.system.domain.valueobject.TransferStatus;
import com.brunosong.transfer.system.transfer.service.valueobject.TransType;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

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
    private String id;

    private String learningMaterialId;

    private String dataMigrationInfoId;

    @Enumerated(EnumType.STRING)
    private TransType transType;

    @Enumerated(EnumType.STRING)
    private TransferStatus transferStatus;

    private String createAdminId;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public void updateTransferStatus(TransferStatus transferStatus) {
        this.transferStatus = transferStatus;
    }

}
