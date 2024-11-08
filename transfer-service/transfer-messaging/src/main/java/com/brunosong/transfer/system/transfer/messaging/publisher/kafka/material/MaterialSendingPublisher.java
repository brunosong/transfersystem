package com.brunosong.transfer.system.transfer.messaging.publisher.kafka.material;

import com.brunosong.transfer.system.kafka.transfer.avro.model.LearningMaterialAvroModel;
import com.brunosong.transfer.system.transfer.messaging.mapper.TransferMessagingDataMapper;
import com.brunosong.transfer.system.transfer.service.entity.LearningMaterial;
import com.brunosong.transfer.system.transfer.service.ports.output.message.publisher.LearningMaterialRequestPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MaterialSendingPublisher implements LearningMaterialRequestPublisher {

    private final TransferMessagingDataMapper transferMessagingDataMapper;

    @Override
    public void publish(LearningMaterial learningMaterial) {
        LearningMaterialAvroModel learningMaterialAvroModel =
                transferMessagingDataMapper.learningMaterialToLearningMaterialAvroModel(learningMaterial);

        
    }
}
