package com.brunosong.deployment.system.deployment.service.dataaccess.config;

import com.brunosong.deployment.system.deployment.service.dto.event.DataSourceCacheEvent;
import com.brunosong.deployment.system.deployment.service.entity.SourceConfig;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;
import org.bson.types.ObjectId;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

// MongoDB 연결을 위한 헬퍼 클래스
@Component
@RequiredArgsConstructor
@Slf4j
public class MongoConnectHelper {

    /** 원본 설정마다 하나씩 두는 클라이언트. MongoClient는 안에 연결 풀을 가지므로 만들어 두고 다시 쓴다 */
    private final Map<String, MongoClient> clients = new ConcurrentHashMap<>();

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
        MongoTemplate mongoTemplate = new MongoTemplate(clientFor(sourceConfig), sourceConfig.getDatabase());
        Query query = new Query(Criteria.where("_id").is(new ObjectId(sourceId)));
        Document doc = mongoTemplate.findOne(query, Document.class, sourceConfig.getTableOrCollection());
        return Optional.ofNullable(doc).map(MongoConnectHelper::toJson);
    }

    /** 원본 설정에 맞는 클라이언트를 캐시에서 꺼내고, 없으면 하나만 만든다. 동시에 와도 하나만 만들려고 computeIfAbsent를 쓴다 */
    MongoClient clientFor(SourceConfig sourceConfig) {
        return clients.computeIfAbsent(cacheKey(sourceConfig), key -> createClient(sourceConfig));
    }

    /** 원본 설정으로 클라이언트를 만든다. 비밀번호에 특수문자가 있어도 깨지지 않게 URI 대신 인증 객체로 넘긴다 */
    MongoClient createClient(SourceConfig sourceConfig) {
        MongoClientSettings settings = MongoClientSettings.builder()
                .credential(MongoCredential.createCredential(
                        sourceConfig.getUsername(),
                        sourceConfig.getDatabase(),
                        sourceConfig.getPassword().toCharArray())
                )
                .applyToClusterSettings(builder -> builder.hosts(
                        List.of(new ServerAddress(sourceConfig.getHost(), sourceConfig.getPort()))))
                .build();
        return MongoClients.create(settings);
    }

    /** 원본 설정이 바뀌었다는 이벤트를 받으면 캐시를 비운다. 옛 설정으로 계속 붙지 않게 하려고다 */
    @EventListener
    public void handleDataDeletedEvent(DataSourceCacheEvent event) {
        if (event.getEventType() == DataSourceCacheEvent.EventType.DATA_SOURCE_CACHE_DELETE) {
            closeAll();
            log.info("Mongo client cache cleared");
        }
    }

    /** 캐시의 클라이언트를 모두 닫고 비운다. 앱이 내려갈 때도 불러 연결을 남기지 않는다 */
    @PreDestroy
    public void closeAll() {
        clients.values().forEach(MongoClient::close);
        clients.clear();
    }

    /** 같은 서버, 같은 DB, 같은 계정이면 같은 클라이언트를 쓰도록 키를 만든다. 비밀번호는 키에 넣지 않는다 */
    private String cacheKey(SourceConfig sourceConfig) {
        return "mongo:" + sourceConfig.getHost() + ":" + sourceConfig.getPort() + ":"
                + sourceConfig.getDatabase() + ":" + sourceConfig.getUsername();
    }

    /** 문서를 정해 둔 규칙(JSON_SETTINGS)으로 JSON 바이트로 바꾼다. Mongo 없이 테스트하려고 따로 뺐다 */
    static byte[] toJson(Document document) {
        return document.toJson(JSON_SETTINGS).getBytes(StandardCharsets.UTF_8);
    }
}
