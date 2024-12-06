package com.brunosong.transfer.system.transfer.service.dto.create;

import com.brunosong.transfer.system.transfer.service.valueobject.TransType;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateTransferCommand {

    @NotNull(message = "adminId is null")
    private UUID adminId;

    @NotNull(message = "transType is null")
    private TransType transType;

    @NotNull(message = "materialId is null")
    private String materialId;

    @NotNull(message = "dataMigrationInfoId is null")
    private UUID dataMigrationInfoId;

}
