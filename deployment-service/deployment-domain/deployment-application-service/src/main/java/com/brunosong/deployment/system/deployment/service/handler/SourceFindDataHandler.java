package com.brunosong.deployment.system.deployment.service.handler;

import com.brunosong.deployment.system.deployment.service.entity.SourceConfig;
import com.brunosong.deployment.system.deployment.service.helper.deployment.DeploymentDataConvertHelper;
import com.brunosong.deployment.system.deployment.service.helper.source.SourceFindHelper;
import com.brunosong.deployment.system.deployment.service.ports.output.repository.SourceDocument;
import com.brunosong.deployment.system.deployment.service.valueobject.Curriculum;
import com.brunosong.deployment.system.deployment.service.valueobject.SourceId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SourceFindDataHandler {

    private final SourceFindHelper sourceFindHelper;
    private final DeploymentDataConvertHelper deploymentDataConvertHelper;

    /** 원본 저장소에서 문서를 꺼내 커리큘럼으로 바꾼다. 청크로 나누는 일은 배포가 한다 */
    public Curriculum findCurriculum(SourceId sourceId, SourceConfig sourceConfig) {

        //1. 원본 문서를 꺼낸다.
        SourceDocument document = sourceFindHelper.findSourceDocument(sourceId, sourceConfig);

        //2. 문서를 커리큘럼으로 바꾼다.
        return deploymentDataConvertHelper.toCurriculum(document);
    }
}
