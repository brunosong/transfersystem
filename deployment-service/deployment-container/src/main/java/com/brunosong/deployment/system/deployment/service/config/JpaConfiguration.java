package com.brunosong.deployment.system.deployment.service.config;


import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaAuditing
@EnableJpaRepositories(basePackages = { "com.brunosong.deployment.system.deployment.service.dataaccess" })
@EntityScan(basePackages = { "com.brunosong.deployment.system.deployment.service.dataaccess" } )
@Configuration
public class JpaConfiguration {
}
