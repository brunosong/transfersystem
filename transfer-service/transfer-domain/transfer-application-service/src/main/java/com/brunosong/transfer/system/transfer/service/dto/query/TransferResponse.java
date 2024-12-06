package com.brunosong.transfer.system.transfer.service.dto.query;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
public class TransferResponse {
    @NotNull
    private UUID transferId;
}
