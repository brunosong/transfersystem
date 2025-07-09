package com.brunosong.transfer.system.transfer.service.helper.source;

import com.brunosong.transfer.system.transfer.service.entity.SourceConfig;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.exception.MaterialNotFoundException;
import com.brunosong.transfer.system.transfer.service.mapper.TransferDataMapper;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.SourceConfigRepository;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceConfigId;
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
public class SourceConfigHelper {

    private final SourceConfigRepository sourceConfigRepository;

    public SourceConfig findSourceConfigInfo(SourceConfigId sourceConfigId) {
        return checkAndFindSourceConfigInfo(sourceConfigId);
    }

    // 전송할 데이터 설정 정보를 찾는 메서드
    private SourceConfig checkAndFindSourceConfigInfo(SourceConfigId sourceConfigId) {
        Optional<SourceConfig> sourceConfigInfo = sourceConfigRepository.findSourceConfigInfo(sourceConfigId.getValue());

        if(sourceConfigInfo.isEmpty()) {
            log.warn("Not found sourceContentData id : {}", sourceConfigId.getValue());
            throw new MaterialNotFoundException("Not found sourceContentData id : " + sourceConfigId.getValue());
        }

        return sourceConfigInfo.get();
    }

}
