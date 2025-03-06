package com.brunosong.transfer.system.transfer.service;

import com.brunosong.transfer.system.transfer.service.ports.output.api.TransferDataApiSender;
import com.brunosong.transfer.system.transfer.service.ports.output.message.publisher.TransferDataSendMessagePublisher;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.SourceRepository;
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
    public SourceRepository learningMaterialRepository() {
        return Mockito.mock(SourceRepository.class);
    }

    @Bean
    public TransferDataApiSender learningMaterialApiSendService() {
        return Mockito.mock(TransferDataApiSender.class);
    }

    @Bean
    public TransferDataSendMessagePublisher learningMaterialRequestPublisher() {
        return Mockito.mock(TransferDataSendMessagePublisher.class);
    }

}
