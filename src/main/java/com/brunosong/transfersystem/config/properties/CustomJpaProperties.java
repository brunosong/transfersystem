package com.brunosong.transfersystem.config.properties;

import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@ConfigurationProperties(
        prefix = "spring.custom-jpa"
)
@Primary
public class CustomJpaProperties extends JpaProperties {

    String scanPackagePath;

    public String getScanPackagePath() {
        return scanPackagePath;
    }

    public void setScanPackagePath(String scanPackagePath) {
        this.scanPackagePath = scanPackagePath;
    }
}
