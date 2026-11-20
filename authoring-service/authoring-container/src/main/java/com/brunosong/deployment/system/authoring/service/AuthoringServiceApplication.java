package com.brunosong.deployment.system.authoring.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** 저작 서비스. 콘텐츠팀 대신 커리큘럼을 초안에서 고치고 판으로 공개한다(ADR 0008) */
@SpringBootApplication(scanBasePackages = "com.brunosong.deployment.system.authoring")
public class AuthoringServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthoringServiceApplication.class, args);
    }
}
