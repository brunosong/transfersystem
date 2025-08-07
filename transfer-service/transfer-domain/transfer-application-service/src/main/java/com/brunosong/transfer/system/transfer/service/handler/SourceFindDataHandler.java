package com.brunosong.transfer.system.transfer.service.handler;

import com.brunosong.transfer.system.transfer.service.entity.SourceConfig;
import com.brunosong.transfer.system.transfer.service.helper.source.SourceConfigHelper;
import com.brunosong.transfer.system.transfer.service.helper.source.SourceFindHelper;
import com.brunosong.transfer.system.transfer.service.helper.transfer.TransferDataConvertHelper;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceConfigId;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceContentData;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SourceFindDataHandler {

    private final SourceFindHelper sourceFindHelper;
    private final SourceConfigHelper sourceConfigHelper;
    private final TransferDataConvertHelper transferDataConvertHelper;

    public SourceContentData findSourceContentData(SourceConfigId sourceConfigId, SourceId sourceId) {

        //1. 전송할 데이터 설정 정보를 찾는다.
        SourceConfig sourceConfig = sourceConfigHelper.findSourceConfigInfo(sourceConfigId);

        //2. 전송할 데이터를 찾는다.
        SourceContentData sourceContentData = sourceFindHelper.findSourceContentData(sourceId, sourceConfig);

        //3. 전송할 데이터를 청크 단위로 변환한다.
        List<byte[]> chunkDataList = transferDataConvertHelper.splitCurriculumJsonIntoChunks(sourceContentData);
        sourceContentData.updateChunkDataList(chunkDataList);

        return sourceContentData;
    }
}
