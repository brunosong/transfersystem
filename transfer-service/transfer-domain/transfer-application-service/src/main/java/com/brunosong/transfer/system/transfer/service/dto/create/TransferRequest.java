package com.brunosong.transfer.system.transfer.service.dto.create;

import com.brunosong.transfer.system.transfer.service.valueobject.SourceType;
import com.brunosong.transfer.system.transfer.service.valueobject.TransType;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferRequest {

    @NotNull(message = "dataMigrationInfoId is null")
    private UUID dataMigrationInfoId;

    @NotNull(message = "transType is null")
    private TransType transType;

    @NotNull(message = "sourceId is null")
    private String sourceId;

    @NotNull(message = "sourceType is null")
    private SourceType sourceType;

    @NotNull(message = "sourceConfigId is null")
    private Long sourceConfigId;
}
