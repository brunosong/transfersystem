package com.brunosong.transfer.system.datamigration.service.helper;

import com.brunosong.transfer.system.datamigration.service.DataPersistService;
import com.brunosong.transfer.system.datamigration.service.annotation.TargetType;
import com.brunosong.transfer.system.datamigration.service.domain.entity.DataMigration;
import com.brunosong.transfer.system.datamigration.service.domain.valueobject.SourceContentData;
import com.brunosong.transfer.system.domain.valueobject.BrunoSongServiceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@TargetType(
        type = BrunoSongServiceType.BRUNOSONG_BOOTCAMP
)
public class BootCampDataPersistHelper implements DataPersistService {

    @Override
    public void dataPersist(DataMigration dataMigrationInfo, SourceContentData sourceContentData) {

    }
}
