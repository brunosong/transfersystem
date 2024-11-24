package com.brunosong.transfer.system.transfer.service;

import com.brunosong.transfer.system.transfer.service.ports.output.api.LearningMaterialApiSender;
import com.brunosong.transfer.system.transfer.service.ports.output.message.publisher.LearningMaterialRequestPublisher;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.LearningMaterialRepository;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.TransferLogRepository;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = "com.brunosong.transfer.system")
public class TransferTestConfiguration {

    @Bean
    public TransferLogRepository transferLogRepository() {
        return Mockito.mock(TransferLogRepository.class);
    }

    @Bean
    public LearningMaterialRepository learningMaterialRepository() {
        return Mockito.mock(LearningMaterialRepository.class);
    }

    @Bean
    public LearningMaterialApiSender learningMaterialApiSendService() {
        return Mockito.mock(LearningMaterialApiSender.class);
    }

    @Bean
    public LearningMaterialRequestPublisher learningMaterialRequestPublisher() {
        return Mockito.mock(LearningMaterialRequestPublisher.class);
    }

}
