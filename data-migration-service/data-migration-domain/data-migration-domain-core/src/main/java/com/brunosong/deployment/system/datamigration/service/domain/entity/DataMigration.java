package com.brunosong.deployment.system.datamigration.service.domain.entity;

import com.brunosong.deployment.system.datamigration.service.domain.valueobject.TargetSystemEnvironment;
import com.brunosong.deployment.system.domain.entity.AggregateRoot;
import com.brunosong.deployment.system.domain.valueobject.DataMigrationId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DataMigration extends AggregateRoot<DataMigrationId> {

    private String targetSystem;
    private TargetSystemEnvironment targetSystemEnvironment;

}
