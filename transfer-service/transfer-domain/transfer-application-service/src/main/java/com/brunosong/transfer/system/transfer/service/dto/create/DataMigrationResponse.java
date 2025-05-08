package com.brunosong.transfer.system.transfer.service.dto.create;

import com.brunosong.transfer.system.domain.valueobject.TransferId;
import com.brunosong.transfer.system.domain.valueobject.TransferStatus;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DataMigrationResponse {
    private TransferId transferId;
    private TransferStatus transferStatus;
    private int chunkOffset;
    private String message;
    private int totalChunkSize;

    public void updateTotalChunkSize(int totalChunkSize) {
        this.totalChunkSize = totalChunkSize;
    }
}
