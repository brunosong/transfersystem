package com.brunosong.transfer.system.transfer.service.dto.create;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateTransferResponse {
    private String message;
    private String transferLogId;
}
