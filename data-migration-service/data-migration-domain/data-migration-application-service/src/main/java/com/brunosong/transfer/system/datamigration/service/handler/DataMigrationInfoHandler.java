package com.brunosong.transfer.system.datamigration.service.handler;

import com.brunosong.transfer.system.datamigration.service.domain.entity.DataMigration;
import com.brunosong.transfer.system.datamigration.service.domain.exception.DataMigrationNotFoundException;
import com.brunosong.transfer.system.datamigration.service.ports.output.cache.DataMigrationCachePort;
import com.brunosong.transfer.system.datamigration.service.ports.output.repository.DataMigrationInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 데이터 마이그레이션 정보를 처리하는 핸들러 클래스입니다.
 * 이 클래스는 데이터 마이그레이션 정보를 캐시에서 찾고, 없을 경우 데이터베이스에서 조회하여 캐시에 저장합니다.
 * 또한, 데이터 마이그레이션 정보가 없을 경우 예외를 발생시킵니다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataMigrationInfoHandler {

    private final DataMigrationInfoRepository dataMigrationInfoRepository;
    private final DataMigrationCachePort dataMigrationCachePort;

    public DataMigration findDataMigrationInfo(Long migrationInfoId) {

        Optional<DataMigration> result = dataMigrationCachePort.findById(migrationInfoId);

        if (result.isEmpty()) {
            result = dataMigrationInfoRepository.findById(migrationInfoId);

            if (result.isPresent()) {
                log.info("DataMigration found in repository for ID: {}", migrationInfoId);
                DataMigration dataMigrationInfo = result.get();

                dataMigrationCachePort.save(dataMigrationInfo);
                log.info("DataMigration saved to cache for ID: {}", migrationInfoId);

                return dataMigrationInfo;
            } else {
                log.error("DataMigration not found for ID: {}", migrationInfoId);
                throw new DataMigrationNotFoundException("DataMigration not found for ID: " + migrationInfoId);
            }
        } else {
            log.info("DataMigration found in cache for ID: {}", migrationInfoId);
            return result.get();
        }
    }

}
