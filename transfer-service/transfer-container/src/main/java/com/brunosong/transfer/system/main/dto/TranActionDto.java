package com.brunosong.transfer.system.main.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

public class TranActionDto {

    @Getter
    @Setter
    public static class TranActionReqDto {

        @NotBlank(message = "Target service is required")
        private String targetService;

        @NotBlank(message = "Database profile is required")
        private String dbProfile;

    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class TranActionRespDto {
        private String message;
    }

}
