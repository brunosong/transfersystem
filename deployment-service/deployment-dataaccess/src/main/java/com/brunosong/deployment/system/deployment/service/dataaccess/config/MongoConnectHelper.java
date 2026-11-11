package com.brunosong.deployment.system.deployment.service.dataaccess.config;

import com.brunosong.deployment.system.deployment.service.entity.SourceConfig;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Optional;

// MongoDB 연결을 위한 헬퍼 클래스
@Component
@RequiredArgsConstructor
public class MongoConnectHelper {

    /** 날짜는 ISO-8601(UTC) 문자열, ObjectId는 16진 문자열로 쓴다. 대상이 Mongo 전용 모양을 몰라도 되게 하려고 정한다 */
    private static final JsonWriterSettings JSON_SETTINGS = JsonWriterSettings.builder()
            .outputMode(JsonMode.RELAXED)
            .dateTimeConverter((value, writer) -> writer.writeString(Instant.ofEpochMilli(value).toString()))
            .objectIdConverter((value, writer) -> writer.writeString(value.toHexString()))
            .build();

    /**
     * 원본 문서 하나를 _id로 찾아 JSON 바이트로 돌려준다.
     * Map을 거치지 않고 드라이버가 바로 JSON을 쓰게 해서 Mongo 전용 타입이 정해 둔 모양으로 나가게 한다.
     */
    public Optional<byte[]> queryMongo(SourceConfig sourceConfig, String sourceId) {
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
            return Optional.ofNullable(doc).map(MongoConnectHelper::toJson);
        } catch (Exception e) {
            // 예외 처리 추가
            throw new RuntimeException("Failed to query MongoDB: " + e.getMessage(), e);
        }
    }

    /** 문서를 정해 둔 규칙(JSON_SETTINGS)으로 JSON 바이트로 바꾼다. Mongo 없이 테스트하려고 따로 뺐다 */
    static byte[] toJson(Document document) {
        return document.toJson(JSON_SETTINGS).getBytes(StandardCharsets.UTF_8);
    }
}
