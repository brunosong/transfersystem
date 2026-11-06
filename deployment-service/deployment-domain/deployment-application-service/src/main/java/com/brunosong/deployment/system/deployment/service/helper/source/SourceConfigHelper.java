package com.brunosong.deployment.system.deployment.service.helper.source;

import com.brunosong.deployment.system.deployment.service.entity.SourceConfig;
import com.brunosong.deployment.system.deployment.service.entity.Deployment;
import com.brunosong.deployment.system.deployment.service.exception.MaterialNotFoundException;
import com.brunosong.deployment.system.deployment.service.mapper.DeploymentDataMapper;
import com.brunosong.deployment.system.deployment.service.ports.output.repository.SourceConfigRepository;
import com.brunosong.deployment.system.deployment.service.valueobject.SourceConfigId;
import com.brunosong.deployment.system.deployment.service.valueobject.SourceContentData;
import com.brunosong.deployment.system.deployment.service.valueobject.SourceId;
import com.brunosong.deployment.system.deployment.service.valueobject.SourceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SourceConfigHelper {

    private final SourceConfigRepository sourceConfigRepository;

    public SourceConfig findSourceConfigInfo(SourceConfigId sourceConfigId) {
        return checkAndFindSourceConfigInfo(sourceConfigId);
    }

    // 배포할 데이터 설정 정보를 찾는 메서드
    private SourceConfig checkAndFindSourceConfigInfo(SourceConfigId sourceConfigId) {
        Optional<SourceConfig> sourceConfigInfo = sourceConfigRepository.findSourceConfigInfo(sourceConfigId.getValue());

        if(sourceConfigInfo.isEmpty()) {
            log.warn("Not found sourceContentData id : {}", sourceConfigId.getValue());
            throw new MaterialNotFoundException("Not found sourceContentData id : " + sourceConfigId.getValue());
        }

        return sourceConfigInfo.get();
    }

}
