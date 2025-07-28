package com.brunosong.transfer.system.datamigration.service;

import com.brunosong.transfer.system.datamigration.service.domain.entity.DataMigration;
import com.brunosong.transfer.system.datamigration.service.domain.valueobject.SourceContentData;

public interface DataPersistService {

    void dataPersist(DataMigration dataMigrationInfo, SourceContentData sourceContentData);
}
