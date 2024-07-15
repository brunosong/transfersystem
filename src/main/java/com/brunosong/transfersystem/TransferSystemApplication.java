package com.brunosong.transfersystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.test.EmbeddedKafkaBroker;

@EnableAspectJAutoProxy
@SpringBootApplication
public class TransferSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransferSystemApplication.class, args);
    }


    /* 실제 운영에 쓰이지 않지만 이 프로젝트에선 프로젝트가 기동할때 카푸카를 임베디드로 기동한다. */
    @Bean
    @ConditionalOnProperty(value = "spring.kafka.main-start" , havingValue = "true")
    public EmbeddedKafkaBroker embeddedKafka() {
        return new EmbeddedKafkaBroker(1, true, 1, "brunosong_topic")
                .kafkaPorts(9092);
    }
}
