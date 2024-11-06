package com.brunosong.transfer.system.transfer.service.dto.excution;

import com.brunosong.transfer.system.transfer.service.valueobject.TransType;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExecutionTransferCommand {

    @NotNull(message = "transType is null")
    private TransType transType;

    @NotNull(message = "materialId is null")
    private String materialId;

    @NotNull(message = "adminId is null")
    private String adminId;

}
