package com.brunosong.transfer.system.datamigration.service.dto.create;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CreateDataMigrationInfoCommand {

    @NotNull
    private String destinationService;

    @NotNull
    private String dbEnvironment;

    @NotNull
    private String migrationMode;

    private DbCredentials dbCredentials;

}