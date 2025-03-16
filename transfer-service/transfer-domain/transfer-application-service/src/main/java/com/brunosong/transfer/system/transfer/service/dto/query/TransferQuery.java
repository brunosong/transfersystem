package com.brunosong.transfer.system.transfer.service.dto.query;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferQuery {

    @NotNull
    private String clientId;

    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private String status;
}


