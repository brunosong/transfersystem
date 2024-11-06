package com.brunosong.transfer.system.transfer.service.dto.excution;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExcutionTransferResponse {
    private String message;
    private String transferLogId;
}
