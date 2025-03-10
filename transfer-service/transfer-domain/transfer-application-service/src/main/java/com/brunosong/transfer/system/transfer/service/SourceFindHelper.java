package com.brunosong.transfer.system.transfer.service;

import com.brunosong.transfer.system.transfer.service.exception.MaterialNotFoundException;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.SourceRepository;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceContentData;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceId;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SourceFindHelper {

    private final SourceRepository sourceRepository;

    public SourceContentData findData(SourceType sourceType, SourceId sourceId) {
        if (sourceType == SourceType.LEARNING_MATERIAL) {
            return checkAndFindLearningMaterial(sourceType, sourceId);
        }
        throw new IllegalArgumentException("Unsupported source type: " + sourceType);
    }

    private SourceContentData checkAndFindLearningMaterial(SourceType sourceType, SourceId sourceId) {
        Optional<SourceContentData> sourceContentData = sourceRepository.findData(String.valueOf(sourceId), sourceType);

        if(sourceContentData.isEmpty()) {
            log.warn("Not found sourceContentData id : {}", sourceId);
            throw new MaterialNotFoundException("Not found sourceContentData id : " + sourceId);
        }

        return sourceContentData.get();
    }

}
