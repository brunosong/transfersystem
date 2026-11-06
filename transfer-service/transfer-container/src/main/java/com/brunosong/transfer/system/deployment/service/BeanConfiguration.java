package com.brunosong.transfer.system.deployment.service;

import com.brunosong.transfer.system.deployment.service.DeploymentDomainService;
import com.brunosong.transfer.system.deployment.service.DeploymentDomainServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public DeploymentDomainService deploymentDomainService() {
        return new DeploymentDomainServiceImpl();
    }
}
