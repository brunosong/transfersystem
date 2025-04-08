package com.brunosong.transfer.system.datamigration.service.domain.entity;

import com.brunosong.transfer.system.datamigration.service.domain.valueobject.SourceContentData;
import com.brunosong.transfer.system.datamigration.service.domain.valueobject.TargetSystemEnvironment;
import com.brunosong.transfer.system.domain.entity.AggregateRoot;
import com.brunosong.transfer.system.domain.valueobject.DataMigrationId;
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
    private SourceContentData sourceContentData;

}
