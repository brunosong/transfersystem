package com.brunosong.transfer.system.transfer.service.handler;

import com.brunosong.transfer.system.transfer.service.dto.create.TransferRequest;
import com.brunosong.transfer.system.transfer.service.entity.SourceConfig;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.helper.source.SourceConfigHelper;
import com.brunosong.transfer.system.transfer.service.helper.source.SourceFindHelper;
import com.brunosong.transfer.system.transfer.service.mapper.TransferDataMapper;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceConfigId;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceContentData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SourceFindDataHandler {

    private final SourceFindHelper sourceFindHelper;
    private final SourceConfigHelper sourceConfigHelper;

    public void ssss(Transfer transfer) {

        SourceConfig sourceConfigInfo = sourceConfigHelper.findSourceConfigInfo(transfer.getSourceConfigId());

        SourceContentData sourceContentData =
                sourceFindHelper.findData(transfer.getSourceType(), transfer.getSourceId());
    }
}
