package com.brunosong.transfer.system.datamigration.service.helper;

import com.brunosong.transfer.system.datamigration.service.domain.entity.DataMigration;
import com.brunosong.transfer.system.datamigration.service.domain.valueobject.SourceContentData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataPersistHandler {

    private final OnlineCampusDataPersistHelper onlineCampusDataPersistHelper;

    public long persist(DataMigration dataMigrationInfo, SourceContentData sourceContentData) {

        if (dataMigrationInfo.getTargetSystem().equals("BRUNOSONG_ONLINE_CAMPUS")) {
            return onlineCampusDataPersistHelper.onlineCampusPersist(sourceContentData);
        }
        return 0;
    }
}
