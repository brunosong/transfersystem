package com.brunosong.deployment.system.deployment.service.handler;

import com.brunosong.deployment.system.deployment.service.entity.Deployment;
import com.brunosong.deployment.system.deployment.service.entity.SourceConfig;
import com.brunosong.deployment.system.deployment.service.helper.deployment.DeploymentDataConvertHelper;
import com.brunosong.deployment.system.deployment.service.helper.source.SourceConfigHelper;
import com.brunosong.deployment.system.deployment.service.helper.source.SourceFindHelper;
import com.brunosong.deployment.system.deployment.service.ports.output.repository.SourceDocument;
import com.brunosong.deployment.system.deployment.service.valueobject.ChunkContent;
import com.brunosong.deployment.system.deployment.service.valueobject.Curriculum;
import com.brunosong.deployment.system.deployment.service.valueobject.SourceContentData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SourceFindDataHandler {

    private final SourceFindHelper sourceFindHelper;
    private final SourceConfigHelper sourceConfigHelper;
    private final DeploymentDataConvertHelper deploymentDataConvertHelper;

    public SourceContentData findSourceContentData(Deployment deployment) {

        //1. 어느 원본 저장소에서 읽을지 찾는다.
        SourceConfig sourceConfig = sourceConfigHelper.findSourceConfigInfo(deployment.getSourceConfigId());

        //2. 원본 문서를 꺼낸다.
        SourceDocument document = sourceFindHelper.findSourceDocument(deployment.getSourceId(), sourceConfig);

        //3. 문서를 커리큘럼으로 바꾼다.
        Curriculum curriculum = deploymentDataConvertHelper.toCurriculum(document);

        //4. 커리큘럼을 청크로 나눈다.
        // SourceContentData 는 5단계에서 지운다. 뒤쪽 코드(Deployment, 매퍼)가 아직 이것을 받아서 여기서 한 번에 만든다
        return SourceContentData.builder()
                .sourceId(document.sourceId())
                .sourceType(document.sourceType())
                .dbType(sourceConfig.getDbType())
                .curriculumId(curriculum.curriculumId())
                .chunkDataList(ChunkContent.split(curriculum))
                .build();
    }
}
