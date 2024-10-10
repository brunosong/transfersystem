package com.brunosong.transfer.system.transfer.service.dto.excution;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExcutionTransferCommand {

    @NotNull(message = "courseSeq is null")
    private Long courseSeq;

}
