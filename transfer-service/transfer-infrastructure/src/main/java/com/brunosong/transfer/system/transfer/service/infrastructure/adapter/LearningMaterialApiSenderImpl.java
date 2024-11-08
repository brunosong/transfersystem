package com.brunosong.transfer.system.transfer.service.infrastructure.adapter;

import com.brunosong.transfer.system.transfer.service.entity.LearningMaterial;
import com.brunosong.transfer.system.transfer.service.ports.output.api.LearningMaterialApiSender;
import org.springframework.stereotype.Service;

@Service
public class LearningMaterialApiSenderImpl implements LearningMaterialApiSender {

//    private final LoadTargetServiceClient loadTargetServiceClient;

    @Override
    public void sendLearningMaterial(LearningMaterial learningMaterial) {
        System.out.println(learningMaterial);
        System.out.println("API 호출");
    }

}
