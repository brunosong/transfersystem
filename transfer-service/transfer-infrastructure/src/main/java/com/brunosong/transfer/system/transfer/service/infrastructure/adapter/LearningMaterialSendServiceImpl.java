package com.brunosong.transfer.system.transfer.service.infrastructure.adapter;

import com.brunosong.transfer.system.transfer.service.entity.LearningMaterial;
import com.brunosong.transfer.system.transfer.service.infrastructure.feign.LoadTargetServiceClient;
import com.brunosong.transfer.system.transfer.service.ports.output.service.LearningMaterialSendService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LearningMaterialSendServiceImpl implements LearningMaterialSendService {

    private final LoadTargetServiceClient loadTargetServiceClient;

    @Override
    public void sendLearningMaterial(LearningMaterial learningMaterial) {



    }

}
