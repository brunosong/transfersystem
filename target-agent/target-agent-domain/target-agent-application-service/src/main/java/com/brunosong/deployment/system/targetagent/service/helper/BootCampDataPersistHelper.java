package com.brunosong.deployment.system.targetagent.service.helper;

import com.brunosong.deployment.system.targetagent.service.DataPersistService;
import com.brunosong.deployment.system.targetagent.service.annotation.ServiceTypeSelector;
import com.brunosong.deployment.system.targetagent.service.domain.valueobject.AppliedContent;
import com.brunosong.deployment.system.targetagent.service.domain.valueobject.ChunkContent;
import com.brunosong.deployment.system.targetagent.service.domain.valueobject.TargetSystemEnvironment;
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

    // 전에는 아무것도 저장하지 않고 0 을 돌려줘 성공으로 알려졌다. 반영하지 않은 것을 반영했다고 알리면 안 된다
    @Override
    public AppliedContent dataPersist(TargetSystemEnvironment environment, String curriculumId, ChunkContent content) {
        throw new UnsupportedOperationException("부트캠프 반영은 아직 구현되지 않았습니다");
    }
}
