package com.brunosong.transfer.system.transfer.service.handler;

import com.brunosong.transfer.system.transfer.service.entity.SourceConfig;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.helper.source.SourceConfigHelper;
import com.brunosong.transfer.system.transfer.service.helper.source.SourceFindHelper;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceContentData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SourceFindDataHandler {

    private final SourceFindHelper sourceFindHelper;
    private final SourceConfigHelper sourceConfigHelper;

    public SourceContentData findSourceContentData(Transfer transfer) {

        //1. 전송할 데이터 설정 정보를 찾는다.
        SourceConfig sourceConfig = sourceConfigHelper.findSourceConfigInfo(transfer.getSourceConfigId());

        //2. 전송할 데이터를 찾는다.
        return sourceFindHelper.findSourceContentData(transfer.getSourceId(), sourceConfig);
    }
}
