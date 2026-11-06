package com.brunosong.transfer.system.deployment.service.handler;

import com.brunosong.transfer.system.deployment.service.valueobject.ChunkContent;
import com.brunosong.transfer.system.deployment.service.entity.SourceConfig;
import com.brunosong.transfer.system.deployment.service.helper.source.SourceConfigHelper;
import com.brunosong.transfer.system.deployment.service.helper.source.SourceFindHelper;
import com.brunosong.transfer.system.deployment.service.helper.deployment.DeploymentDataConvertHelper;
import com.brunosong.transfer.system.deployment.service.valueobject.SourceConfigId;
import com.brunosong.transfer.system.deployment.service.valueobject.SourceContentData;
import com.brunosong.transfer.system.deployment.service.valueobject.SourceId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SourceFindDataHandler {

    private final SourceFindHelper sourceFindHelper;
    private final SourceConfigHelper sourceConfigHelper;
    private final DeploymentDataConvertHelper deploymentDataConvertHelper;

    public SourceContentData findSourceContentData(SourceConfigId sourceConfigId, SourceId sourceId) {

        //1. 배포할 데이터 설정 정보를 찾는다.
        SourceConfig sourceConfig = sourceConfigHelper.findSourceConfigInfo(sourceConfigId);

        //2. 배포할 데이터를 찾는다.
        SourceContentData sourceContentData = sourceFindHelper.findSourceContentData(sourceId, sourceConfig);

        //3. 배포할 데이터를 청크 단위로 변환한다.
        List<ChunkContent> chunkDataList = deploymentDataConvertHelper.splitCurriculumJsonIntoChunks(sourceContentData);
        sourceContentData.updateChunkDataList(chunkDataList);

        return sourceContentData;
    }
}
