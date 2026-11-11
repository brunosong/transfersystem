package com.brunosong.deployment.system.deployment.service.dataaccess.config;

import com.brunosong.deployment.system.deployment.service.dto.event.DataSourceCacheEvent;
import com.brunosong.deployment.system.deployment.service.entity.SourceConfig;
import com.mongodb.client.MongoClient;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class MongoConnectHelperTest {

    /** 날짜와 ObjectId가 대상이 읽기 쉬운 문자열로 바뀌는지 확인한다. attributes로 그대로 나가는 값이라서다 */
    @Test
    @DisplayName("Mongo 문서를 JSON으로 바꾸면 ObjectId는 16진 문자열, 날짜는 ISO-8601(UTC) 문자열, 숫자는 숫자 그대로 나간다")
    void mongoTypesBecomePlainStrings() {
        Document doc = new Document("_id", new ObjectId("6731a2b4c5d6e7f801234567"))
                .append("createdAt", Date.from(Instant.parse("2026-11-11T00:00:00Z")))
                .append("difficulty", 3);

        String json = new String(MongoConnectHelper.toJson(doc), StandardCharsets.UTF_8);

        assertEquals("{\"_id\": \"6731a2b4c5d6e7f801234567\", \"createdAt\": \"2026-11-11T00:00:00Z\", \"difficulty\": 3}", json);
    }

    /** 같은 원본 설정이면 클라이언트를 한 번만 만드는지 확인한다. 요청마다 연결 풀을 새로 만들던 문제를 막는다 */
    @Test
    @DisplayName("같은 원본 설정(서버, DB, 계정)으로 두 번 조회하면 Mongo 클라이언트(연결 풀)는 한 번만 만들고 두 번째는 캐시에서 꺼내 쓴다")
    void sameSourceReusesClient() {
        MongoConnectHelper helper = Mockito.spy(new MongoConnectHelper());
        MongoClient client = Mockito.mock(MongoClient.class);
        Mockito.doReturn(client).when(helper).createClient(Mockito.any());

        helper.clientFor(source());
        helper.clientFor(source());

        Mockito.verify(helper, Mockito.times(1)).createClient(Mockito.any());
    }

    /** 캐시를 비우는 이벤트가 오면 클라이언트를 닫고, 다음 요청에서 새로 만드는지 확인한다 */
    @Test
    @DisplayName("캐시 삭제 이벤트를 받으면 캐시에 있던 Mongo 클라이언트를 닫고 비워서, 다음 조회 때 클라이언트를 새로 만든다")
    void cacheEventClosesClient() {
        MongoConnectHelper helper = Mockito.spy(new MongoConnectHelper());
        MongoClient client = Mockito.mock(MongoClient.class);
        Mockito.doReturn(client).when(helper).createClient(Mockito.any());
        helper.clientFor(source());

        helper.handleDataDeletedEvent(new DataSourceCacheEvent(this, DataSourceCacheEvent.EventType.DATA_SOURCE_CACHE_DELETE));
        helper.clientFor(source());

        Mockito.verify(client).close();
        Mockito.verify(helper, Mockito.times(2)).createClient(Mockito.any());
    }

    /** 테스트용 원본 설정 */
    private SourceConfig source() {
        return SourceConfig.builder().host("localhost").port(27017).database("curriculum")
                .username("reader").password("secret").build();
    }
}