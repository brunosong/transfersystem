package com.brunosong.transfer.system.transfer.service.dataaccess.config;

import com.brunosong.transfer.system.transfer.service.entity.SourceConfig;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

// MongoDB 연결을 위한 헬퍼 클래스
@Component
@RequiredArgsConstructor
public class MongoConnectHelper {

    public Optional<Map<String, Object>> queryMongo(SourceConfig sourceConfig, String sourceId) {
        String mongoUri = String.format(
                "mongodb://%s:%s@%s:%d/%s?authSource=%s",
                sourceConfig.getUsername(),
                sourceConfig.getPassword(),
                sourceConfig.getHost(),
                sourceConfig.getPort(),
                sourceConfig.getDatabase(),
                sourceConfig.getDatabase()
        );

        try (MongoClient mongoClient = MongoClients.create(mongoUri)) {
            MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, sourceConfig.getDatabase());

            Query query = new Query(Criteria.where("_id").is(new ObjectId(sourceId)));
            Document doc = mongoTemplate.findOne(query, Document.class, sourceConfig.getTableOrCollection());
            return Optional.ofNullable(doc)
                    .map(d -> (Map<String, Object>) d);
        } catch (Exception e) {
            // 예외 처리 추가
            throw new RuntimeException("Failed to query MongoDB: " + e.getMessage(), e);
        }
    }
}
