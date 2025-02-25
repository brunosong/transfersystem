package com.brunosong.transfer.system.datamigration.service.dto.message;

import com.brunosong.transfer.system.domain.valueobject.TransferId;
import com.brunosong.transfer.system.domain.valueobject.TransferStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DataMigrationStatusOutboxMessage {
    private TransferId transferId;
    private TransferStatus transferStatus;
}
