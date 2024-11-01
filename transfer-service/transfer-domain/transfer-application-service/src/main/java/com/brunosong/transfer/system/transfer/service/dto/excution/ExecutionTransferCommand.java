package com.brunosong.transfer.system.transfer.service.dto.excution;

import com.brunosong.transfer.system.transfer.service.valueobject.TransType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExecutionTransferCommand {

    @NotNull(message = "transType is null")
    private TransType transType;

    @NotNull(message = "materialId is null")
    private String materialId;

    @NotNull(message = "adminId is null")
    private String adminId;

}
