package com.brunosong.deployment.system.deployment.service.helper.source;

import com.brunosong.deployment.system.deployment.service.entity.SourceConfig;
import com.brunosong.deployment.system.deployment.service.exception.MaterialNotFoundException;
import com.brunosong.deployment.system.deployment.service.ports.output.repository.SourceDocument;
import com.brunosong.deployment.system.deployment.service.ports.output.repository.SourceRepository;
import com.brunosong.deployment.system.deployment.service.valueobject.SourceId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SourceFindHelper {

    private final SourceRepository sourceRepository;

    /** 원본 저장소에서 문서 하나를 꺼낸다. 없으면 MaterialNotFoundException 을 던진다 */
    public SourceDocument findSourceDocument(SourceId sourceId, SourceConfig sourceConfig) {
        return sourceRepository.findData(sourceId.getValue(), sourceConfig)
                .orElseThrow(() -> {
                    log.warn("Not found source document id : {}", sourceId.getValue());
                    return new MaterialNotFoundException("Not found source document id : " + sourceId.getValue());
                });
    }

}
