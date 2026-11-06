package com.brunosong.deployment.system.targetagent.service.handler;

import com.brunosong.deployment.system.targetagent.service.DataPersistService;
import com.brunosong.deployment.system.targetagent.service.annotation.ServiceTypeSelector;
import com.brunosong.deployment.system.targetagent.service.domain.valueobject.AppliedContent;
import com.brunosong.deployment.system.targetagent.service.domain.valueobject.ChunkContent;
import com.brunosong.deployment.system.targetagent.service.domain.valueobject.TargetSystemEnvironment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * 메시지의 대상 시스템에 맞는 어댑터를 찾아 반영을 맡긴다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataPersistHandler {

    // 계약의 값(ONLINE_CAMPUS)과 코드의 enum(BRUNOSONG_ONLINE_CAMPUS)이 아직 다르다. 이름을 바꿀 때 없앤다
    private static final String SERVICE_TYPE_PREFIX = "BRUNOSONG_";

    private final List<DataPersistService> dataPersistService;

    /** 이 에이전트가 맡은 대상 시스템인가. 맡지 않은 대상의 명령은 결과 없이 건너뛴다 */
    public boolean supports(String targetSystem) {
        return findService(targetSystem).isPresent();
    }

    public AppliedContent persist(String targetSystem, TargetSystemEnvironment environment,
                                  String curriculumId, ChunkContent content) {
        DataPersistService service = findService(targetSystem)
                .orElseThrow(() -> new IllegalArgumentException("맡지 않은 대상 시스템입니다: " + targetSystem));

        log.info("DataPersistService found for target system: {}", targetSystem);
        return service.dataPersist(environment, curriculumId, content);
    }

    private Optional<DataPersistService> findService(String targetSystem) {
        return dataPersistService.stream()
                .filter(service -> matches(service, targetSystem))
                .findFirst();
    }

    // @Transactional 이 있는 어댑터는 프록시로 감싸지므로 getClass() 에서 애너테이션을 바로 찾을 수 없다.
    // findAnnotation 은 상위 클래스까지 찾아 준다
    private boolean matches(DataPersistService service, String targetSystem) {
        ServiceTypeSelector selector = AnnotationUtils.findAnnotation(service.getClass(), ServiceTypeSelector.class);
        return selector != null && selector.type().name().equals(SERVICE_TYPE_PREFIX + targetSystem);
    }
}
