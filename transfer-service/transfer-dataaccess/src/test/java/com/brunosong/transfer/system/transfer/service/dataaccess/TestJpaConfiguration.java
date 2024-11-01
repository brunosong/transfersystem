package com.brunosong.transfer.system.transfer.service.dataaccess;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "com.brunosong.transfer.system.transfer.service.dataaccess.transfer")
@EntityScan(basePackages = { "com.brunosong.transfer.system.transfer.service.dataaccess.transfer" })
@SpringBootConfiguration
public class TestJpaConfiguration {


}
