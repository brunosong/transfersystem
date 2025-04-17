package com.brunosong.transfer.system.datamigration.service.dto.message;

import com.brunosong.transfer.system.domain.valueobject.TransferStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DataMigrationResponseMessage {
    private int chunkOffset;
    private String transferId;
    private String message;
    private TransferStatus transferStatus;
}
