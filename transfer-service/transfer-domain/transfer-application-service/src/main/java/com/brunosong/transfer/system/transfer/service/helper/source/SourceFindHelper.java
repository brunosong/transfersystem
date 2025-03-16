package com.brunosong.transfer.system.transfer.service.helper.source;

import com.brunosong.transfer.system.transfer.service.entity.SourceConfig;
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

    public SourceContentData findData(SourceId sourceId, SourceConfig sourceConfig) {
        Optional<SourceContentData> sourceContentData = sourceRepository.findData(sourceId.getValue(), sourceConfig);

        if(sourceContentData.isEmpty()) {
            log.warn("Not found sourceContentData id : {}", sourceId);
            throw new MaterialNotFoundException("Not found sourceContentData id : " + sourceId);
        }

        return sourceContentData.get();
    }

}
