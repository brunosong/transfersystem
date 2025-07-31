package com.brunosong.transfer.system.datamigration.service.handler;

import com.brunosong.transfer.system.datamigration.service.DataPersistService;
import com.brunosong.transfer.system.datamigration.service.annotation.ServiceTypeSelector;
import com.brunosong.transfer.system.datamigration.service.domain.entity.DataMigration;
import com.brunosong.transfer.system.datamigration.service.domain.valueobject.SourceContentData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * 이 클래스는 데이터 마이그레이션 정보를 처리할 수 있는 서비스들을 찾아서 해당 서비스를 실행 하는 핸들러입니다.
 * 만약 해당하는 서비스가 없을 경우 예외를 발생시킵니다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataPersistHandler {

    private final List<DataPersistService> dataPersistService;

    public long persist(DataMigration dataMigrationInfo, SourceContentData sourceContentData) {

        Optional<DataPersistService> optional = dataPersistService.stream()
                .filter(service -> getDataPersistService(service, dataMigrationInfo.getTargetSystem()))
                .findFirst();

        if (optional.isPresent()) {
            log.info("DataPersistService found for target system: {}", dataMigrationInfo.getTargetSystem());
            return optional.get().dataPersist(dataMigrationInfo, sourceContentData);
        } else {
            log.error("No DataPersistService found for target system: {}", dataMigrationInfo.getTargetSystem());
            throw new IllegalArgumentException("No DataPersistService found for target system: " + dataMigrationInfo.getTargetSystem());
        }
    }

    private boolean getDataPersistService(DataPersistService service, String targetSystem) {
        Class<? extends DataPersistService> aClass = service.getClass();

        if (aClass.isAnnotationPresent(ServiceTypeSelector.class))  {
            ServiceTypeSelector serviceTypeSelectorAnnotation = aClass.getAnnotation(ServiceTypeSelector.class);
            return serviceTypeSelectorAnnotation.type().name().equals(targetSystem);
        }

        return false;
    }

}
