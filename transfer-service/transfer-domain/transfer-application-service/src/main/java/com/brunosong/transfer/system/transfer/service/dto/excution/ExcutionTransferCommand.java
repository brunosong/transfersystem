package com.brunosong.transfer.system.transfer.service.dto.excution;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
public class ExcutionTransferCommand {

    @NotBlank(message = "Target service is required")
    private String targetService;

    @NotBlank(message = "Database profile is required")
    private String dbProfile;

}
