package com.brunosong.transfer.system.transfer.service.dto.create;

import com.brunosong.transfer.system.domain.valueobject.TransferId;
import com.brunosong.transfer.system.domain.valueobject.TransferStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DataMigrationResponse {
    private TransferId transferId;
    private TransferStatus transferStatus;
    private int chunkOffset;
    private String message;
}
