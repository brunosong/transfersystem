package com.brunosong.deployment.system.deployment.service.dataaccess.config;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class MongoConnectHelperTest {

    /** 날짜와 ObjectId가 대상이 읽기 쉬운 문자열로 바뀌는지 확인한다. attributes로 그대로 나가는 값이라서다 */
    @Test
    void mongoTypesBecomePlainStrings() {
        Document doc = new Document("_id", new ObjectId("6731a2b4c5d6e7f801234567"))
                .append("createdAt", Date.from(Instant.parse("2026-11-11T00:00:00Z")))
                .append("difficulty", 3);

        String json = new String(MongoConnectHelper.toJson(doc), StandardCharsets.UTF_8);

        assertEquals("{\"_id\": \"6731a2b4c5d6e7f801234567\", \"createdAt\": \"2026-11-11T00:00:00Z\", \"difficulty\": 3}", json);
    }
}