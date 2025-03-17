package com.brunosong.transfer.system.datamigration.service.dto.create;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

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