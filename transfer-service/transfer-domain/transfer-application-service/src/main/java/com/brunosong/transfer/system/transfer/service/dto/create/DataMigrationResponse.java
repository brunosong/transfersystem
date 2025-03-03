package com.brunosong.transfer.system.transfer.service.dto.create;

import com.brunosong.transfer.system.domain.valueobject.TransferStatus;
import lombok.*;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DataMigrationResponse {
    private UUID transferId;
    private TransferStatus transferStatus;
}
