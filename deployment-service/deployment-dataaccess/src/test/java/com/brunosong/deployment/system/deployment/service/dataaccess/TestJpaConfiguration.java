package com.brunosong.deployment.system.deployment.service.dataaccess;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "com.brunosong.deployment.system.deployment.service.dataaccess.deployment")
@EntityScan(basePackages = { "com.brunosong.deployment.system.deployment.service.dataaccess.deployment" })
@SpringBootConfiguration
public class TestJpaConfiguration {


}
