package com.brunosong.transfer.system.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = { "com.brunosong.transfer.system.transfer.service.dataaccess" })
@EntityScan(basePackages = { "com.brunosong.transfer.system.transfer.dataaccess" })
@SpringBootApplication(scanBasePackages = "com.brunosong.transfer.system")
public class TransferSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransferSystemApplication.class, args);
    }


    /* 실제 운영에 쓰이지 않지만 이 프로젝트에선 프로젝트가 기동할때 카푸카를 임베디드로 기동한다. */
//    @Bean
//    @ConditionalOnProperty(value = "spring.kafka.main-start" , havingValue = "true")
//    public EmbeddedKafkaBroker embeddedKafka() {
//        return new EmbeddedKafkaBroker(1, true, 1, "brunosong_topic")
//                .kafkaPorts(9092);
//    }
}
