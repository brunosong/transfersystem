package com.brunosong.transfer.system.transfer.service.infrastructure.adapter;

import com.brunosong.transfer.system.transfer.service.valueobject.LearningMaterial;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.infrastructure.feign.LoadTargetServiceClient;
import com.brunosong.transfer.system.transfer.service.infrastructure.mapper.TransferApiDataMapper;
import com.brunosong.transfer.system.transfer.service.ports.output.api.TransferDataApiSender;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceContentData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferDataApiSenderImpl implements TransferDataApiSender {

    private final TransferApiDataMapper transferApiDataMapper;
    private final LoadTargetServiceClient loadTargetServiceClient;

    @Override
    public void sendLearningMaterial(Transfer transfer) {
        log.info("Api send");
        SourceContentData sourceContentData = transfer.getSourceContentData();
        String message = loadTargetServiceClient.sendMetadata(
                transferApiDataMapper.toLearningMaterialApiModel(transfer, sourceContentData));

        log.info("api result message : %s", message);
    }
}
