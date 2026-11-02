package com.brunosong.transfer.system.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication(scanBasePackages = "com.brunosong.transfer.system")
public class TransferSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(TransferSystemApplication.class, args);
    }
}
