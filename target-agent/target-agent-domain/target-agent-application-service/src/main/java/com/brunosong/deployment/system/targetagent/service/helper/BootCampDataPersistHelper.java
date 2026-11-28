package com.brunosong.deployment.system.targetagent.service.helper;

import com.brunosong.deployment.system.targetagent.service.DataPersistService;
import com.brunosong.deployment.system.targetagent.service.annotation.ServiceTypeSelector;
import com.brunosong.deployment.system.targetagent.service.config.BootcampRoutingDataSourceContextHolder;
import com.brunosong.deployment.system.targetagent.service.domain.valueobject.AppliedContent;
import com.brunosong.deployment.system.targetagent.service.domain.valueobject.ChunkContent;
import com.brunosong.deployment.system.targetagent.service.domain.valueobject.DataSourceType;
import com.brunosong.deployment.system.targetagent.service.domain.valueobject.TargetSystemEnvironment;
import com.brunosong.deployment.system.targetagent.service.ports.output.repository.BootcampRepository;
import com.brunosong.deployment.system.domain.valueobject.BrunoSongServiceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ServiceTypeSelector(
        type = BrunoSongServiceType.BRUNOSONG_BOOTCAMP
)
public class BootCampDataPersistHelper implements DataPersistService {

    private final BootcampRepository bootcampRepository;

    @Override
    public AppliedContent dataPersist(TargetSystemEnvironment environment, String curriculumId, ChunkContent content) {
        // 트랜잭션이 커넥션을 잡기 전에 어느 DB인지 정해 둔다. 저장소가 트랜잭션을 연다
        BootcampRoutingDataSourceContextHolder.setDataSourceType(environment == TargetSystemEnvironment.PROD
                ? DataSourceType.BRUNOSONG_BOOTCAMP_PROD
                : DataSourceType.BRUNOSONG_BOOTCAMP_DEV);
        try {
            log.info("Persisting data for Bootcamp in {} environment", environment);
            return bootcampRepository.saveOrUpdate(curriculumId, content);
        } finally {
            BootcampRoutingDataSourceContextHolder.clearDataSourceType();
        }
    }
}
