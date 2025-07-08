package com.brunosong.transfer.system.transfer.service.dataaccess.config;

import com.brunosong.transfer.system.transfer.service.entity.SourceConfig;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceType;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class DynamicDataSourceConnector {

    private final MongoConnectHelper mongoConnectHelper;
    private final MysqlConnectHelper mysqlConnectHelper;

    /**
     * 동적 데이터 소스 생성
     * @param sourceConfig 데이터 소스 설정 정보
     * @return 생성된 DataSource 객체
     */
    public Optional<Map<String, Object>> query(SourceConfig sourceConfig, String sourceId) {

        return switch (sourceConfig.getDbType()) {
            case MONGO -> mongoConnectHelper.queryMongo(sourceConfig, sourceId);
            case MYSQL -> mysqlConnectHelper.queryMySql(sourceConfig, sourceId);
            case POSTGRESQL -> null;
            case ORACLE -> null;
            case H2 -> null;
        };
    }

}
