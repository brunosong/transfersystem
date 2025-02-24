package com.brunosong.transfer.system.transfer.service.dto.create;

import com.brunosong.transfer.system.domain.valueobject.TransferId;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateTransferResponse {
    private String logMessage;
    private TransferId transferLogId;
}
