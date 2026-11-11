package com.brunosong.deployment.system.deployment.service.dataaccess.config;

import com.brunosong.deployment.system.deployment.service.entity.SourceConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DynamicDataSourceConnector {

    private final MongoConnectHelper mongoConnectHelper;
    private final MysqlConnectHelper mysqlConnectHelper;
    private final ObjectMapper objectMapper;

    /**
     * 원본 설정의 DB 종류에 맞는 방법으로 데이터를 찾아 JSON 바이트로 돌려준다.
     * 원본마다 JSON을 만드는 방법이 달라서 여기서 통일한다.
     */
    public Optional<byte[]> query(SourceConfig sourceConfig, String sourceId) {

        return switch (sourceConfig.getDbType()) {
            case MONGO -> mongoConnectHelper.queryMongo(sourceConfig, sourceId);
            case MYSQL -> mysqlConnectHelper.queryMySql(sourceConfig, sourceId).map(this::toJson);
            case POSTGRESQL,ORACLE,H2 ->
                    throw new UnsupportedOperationException("지원하지 않는 원본 DB: " + sourceConfig.getDbType());
        };
    }

    /**
     * MySQL 행(Map)을 JSON 바이트로 바꾼다. Spring의 ObjectMapper를 써서 날짜가 ISO 문자열로 나가게 한다
     */
    private byte[] toJson(Map<String, Object> row) {
        try {
            return objectMapper.writeValueAsBytes(row);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("원본 행을 JSON으로 바꾸지 못했다", e);
        }
    }
}