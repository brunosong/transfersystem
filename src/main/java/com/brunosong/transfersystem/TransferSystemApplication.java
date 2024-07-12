package com.brunosong.transfersystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@SpringBootApplication
public class TransferSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransferSystemApplication.class, args);
    }

}
