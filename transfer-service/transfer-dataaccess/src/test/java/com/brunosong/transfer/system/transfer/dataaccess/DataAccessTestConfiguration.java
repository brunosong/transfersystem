package com.brunosong.transfer.system.transfer.dataaccess;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

//@SpringBootApplication(scanBasePackages = "com.brunosong.transfer.system.transfer.dataaccess")
@EnableJpaRepositories(basePackages = { "com.brunosong.transfer.system.transfer.dataaccess"})
@EntityScan(basePackages = { "com.brunosong.transfer.system.transfer.dataaccess" })
@SpringBootConfiguration
public class DataAccessTestConfiguration {
}
