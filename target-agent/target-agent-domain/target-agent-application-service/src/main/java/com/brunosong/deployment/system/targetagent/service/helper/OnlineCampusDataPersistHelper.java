package com.brunosong.deployment.system.targetagent.service.helper;

import com.brunosong.deployment.system.targetagent.service.DataPersistService;
import com.brunosong.deployment.system.targetagent.service.annotation.ServiceTypeSelector;
import com.brunosong.deployment.system.targetagent.service.config.OnlineCampusRoutingDataSourceContextHolder;
import com.brunosong.deployment.system.targetagent.service.domain.valueobject.AppliedContent;
import com.brunosong.deployment.system.targetagent.service.domain.valueobject.ChunkContent;
import com.brunosong.deployment.system.targetagent.service.domain.valueobject.DataSourceType;
import com.brunosong.deployment.system.targetagent.service.domain.valueobject.TargetSystemEnvironment;
import com.brunosong.deployment.system.targetagent.service.dto.message.ApplyOutcome;
import com.brunosong.deployment.system.targetagent.service.ports.output.repository.CurriculumRepository;
import com.brunosong.deployment.system.domain.valueobject.BrunoSongServiceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ServiceTypeSelector(
        type = BrunoSongServiceType.BRUNOSONG_ONLINE_CAMPUS
)
public class OnlineCampusDataPersistHelper implements DataPersistService {

    private final CurriculumRepository curriculumRepository;

    @Override
    public AppliedContent dataPersist(TargetSystemEnvironment targetSystemEnvironment, String curriculumId, ChunkContent content) {

        try {
            if (targetSystemEnvironment == TargetSystemEnvironment.PROD) {
                log.info("Persisting data for Online Campus in PROD environment");
                OnlineCampusRoutingDataSourceContextHolder.setDataSourceType(DataSourceType.BRUNOSONG_ONLINE_CAMPUS_PROD);
            } else {
                OnlineCampusRoutingDataSourceContextHolder.setDataSourceType(DataSourceType.BRUNOSONG_ONLINE_CAMPUS_DEV);
                log.info("Persisting data for Online Campus in DEV environment");
            }
            return applyToOnlineCampus(curriculumId, content);

        } finally {
            OnlineCampusRoutingDataSourceContextHolder.clearDataSourceType();
        }
    }

    // 트랜잭션은 저장소(CurriculumRepositoryImpl)가 대상 DB의 트랜잭션 매니저로 연다.
    // 전에는 여기에 @Transactional 이 있었지만 같은 클래스 안에서 불러 적용되지 않았다(self-invocation)
    private AppliedContent applyToOnlineCampus(String curriculumId, ChunkContent content) {
        ApplyOutcome applyOutcome = curriculumRepository.saveOrUpdate(curriculumId, content);
        log.info("Successfully saved content with id: {}", applyOutcome.id());

        return applyOutcome.applied();
    }
}
