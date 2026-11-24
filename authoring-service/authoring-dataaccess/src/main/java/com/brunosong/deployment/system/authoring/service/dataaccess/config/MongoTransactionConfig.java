package com.brunosong.deployment.system.authoring.service.dataaccess.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;

/**
 * Mongo 트랜잭션을 켠다. Spring Boot 는 Mongo 트랜잭션 관리자를 저절로 만들지 않는다.
 * 이 빈이 있어야 @Transactional 이 Mongo 쓰기를 한 트랜잭션으로 묶는다.
 * 트랜잭션은 레플리카 셋에서만 된다(로컬 Mongo 를 rs0 으로 바꾼 이유, 시스템 설계 3장)
 */
@Configuration
public class MongoTransactionConfig {

    @Bean
    public MongoTransactionManager transactionManager(MongoDatabaseFactory factory) {
        return new MongoTransactionManager(factory);
    }
}
