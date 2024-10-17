package com.brunosong.transfer.system.dataupload.service.domain;

import com.brunosong.transfer.system.dataaccess.meterials.entity.LearningMaterialMongoEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.InputStream;
import java.util.List;

@Configuration
public class MongoInitConfig {

    @Bean
    public CommandLineRunner initMongoWithJson(MongoTemplate mongoTemplate, ObjectMapper objectMapper) {
        return args -> {
            // JSON 파일을 읽어와서 List로 변환
            InputStream inputStream = getClass().getResourceAsStream("/data/init-learning-material.json");
            List<LearningMaterialMongoEntity> materials = objectMapper.readValue(
                    inputStream, new TypeReference<>() {}
            );

            // 데이터를 MongoDB에 삽입
            mongoTemplate.insertAll(materials);
            System.out.println("MongoDB 초기화 완료.");

            // View 이름과 원본 컬렉션
            String viewName = "learningMaterialView";
            String sourceCollection = "learningMaterial";

            // Aggregation Pipeline 정의
            Document matchStage = new Document("$match",
                    new Document("learningLevel", new Document("$gte", 1)));
            Document projectStage = new Document("$project",
                    new Document("title", 1).append("description", 1).append("_id", 0));

            // 기존 View 삭제 (필요한 경우)
            mongoTemplate.getDb().getCollection(viewName).drop();

            // View 생성
            mongoTemplate.getDb().createView(viewName, sourceCollection,
                    List.of(matchStage, projectStage));
        };
    }
}
