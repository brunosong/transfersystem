package com.brunosong.transfer.system.main;

import com.brunosong.transfer.system.transfer.service.TransferDomainService;
import com.brunosong.transfer.system.transfer.service.TransferDomainServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public TransferDomainService transferDomainService() {
        return new TransferDomainServiceImpl();
    }
}
