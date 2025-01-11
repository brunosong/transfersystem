package com.brunosong.transfer.system.transfer.service.infrastructure.adapter;

import com.brunosong.transfer.system.transfer.service.entity.LearningMaterial;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.infrastructure.feign.LoadTargetServiceClient;
import com.brunosong.transfer.system.transfer.service.infrastructure.mapper.TransferApiDataMapper;
import com.brunosong.transfer.system.transfer.service.ports.output.api.LearningMaterialApiSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LearningMaterialApiSenderImpl implements LearningMaterialApiSender {

    private final TransferApiDataMapper transferApiDataMapper;
    private final LoadTargetServiceClient loadTargetServiceClient;

    @Override
    public void sendLearningMaterial(Transfer transfer, LearningMaterial learningMaterial) {
        log.info("Api send");
        String message = loadTargetServiceClient.sendMetadata(
                transferApiDataMapper.toLearningMaterialApiModel(transfer, learningMaterial));

        log.info("api result message : %s", message);
    }
}
