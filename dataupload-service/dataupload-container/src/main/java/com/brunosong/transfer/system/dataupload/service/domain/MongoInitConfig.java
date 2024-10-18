package com.brunosong.transfer.system.dataupload.service.domain;

import com.brunosong.transfer.system.dataaccess.meterials.entity.LearningMaterialViewEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Configuration
public class MongoInitConfig {

    @Bean
    public CommandLineRunner initMongoWithJson(MongoTemplate mongoTemplate, ObjectMapper objectMapper) {
        return args -> {

            // View 이름과 원본 컬렉션
            String collectionName = "learningMaterial";
            String viewName = "learningMaterialView";


            // JSON 파일을 읽어와서 List로 변환
            InputStream inputStream = getClass().getResourceAsStream("/data/init-learning-material.json");
            List<Map<String,Object>> materials = objectMapper.readValue(
                    inputStream, new TypeReference<>() {}
            );

            // 기존 컬렉션 삭제
            mongoTemplate.getDb().getCollection(collectionName).drop();

            // 데이터를 MongoDB에 삽입
            mongoTemplate.insert(materials,collectionName);
            System.out.println("MongoDB 초기화 완료.");


            // Aggregation Pipeline 정의
//            Document matchStage = new Document("$match",
//                    new Document("learningLevel", new Document("$gte", 1)));
            Document projectStage = new Document("$project",
                    new Document("title", 1).append("description", 1).append("_id", 1));

            // 기존 View 삭제 (필요한 경우)
            mongoTemplate.getDb().getCollection(viewName).drop();

            // View 생성
            mongoTemplate.getDb().createView(viewName, collectionName,
                    List.of(//matchStage,
                            projectStage));

            System.out.println("View 생성 완료.");
        };
    }
}
