package com.brunosong.transfer.system.dataupload.service.domain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.brunosong.transfer.system")
public class DataUploadServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataUploadServiceApplication.class,args);
    }

}
