package com.brunosong.transfer.system.datamigration.service.cache.datamigrationinfo.adapter;

import com.brunosong.transfer.system.datamigration.service.domain.entity.DataMigration;
import com.brunosong.transfer.system.datamigration.service.ports.output.cache.DataMigrationCachePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DataMigrationCacheAdapter implements DataMigrationCachePort {

    @Override
    public void save(DataMigration dataMigration) {
        // 캐쉬 TTL은 10분으로 준다.
    }

    @Override
    public Optional<DataMigration> findById(Long migrationInfoId) {
        return Optional.empty();
    }
}
