package com.brunosong.transfer.system.deployment.service.helper.source;

import com.brunosong.transfer.system.deployment.service.entity.SourceConfig;
import com.brunosong.transfer.system.deployment.service.exception.MaterialNotFoundException;
import com.brunosong.transfer.system.deployment.service.ports.output.repository.SourceRepository;
import com.brunosong.transfer.system.deployment.service.valueobject.SourceContentData;
import com.brunosong.transfer.system.deployment.service.valueobject.SourceId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SourceFindHelper {

    private final SourceRepository sourceRepository;

    // 배포할 데이터를 찾는 메서드
    public SourceContentData findSourceContentData(SourceId sourceId, SourceConfig sourceConfig) {
        Optional<SourceContentData> sourceContentData = sourceRepository.findData(sourceId.getValue(), sourceConfig);

        if (sourceContentData.isEmpty()) {
            log.warn("Not found sourceContentData id : {}", sourceId);
            throw new MaterialNotFoundException("Not found sourceContentData id : " + sourceId);
        }

        return sourceContentData.get();
    }

}
