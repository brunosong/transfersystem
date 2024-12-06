package com.brunosong.transfer.system.transfer.service.dto.query;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
public class TransferQuery {

    @NotNull
    private String clientId;

    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private String status;
}
