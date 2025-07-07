package com.brunosong.transfer.system.transfer.service.dataaccess.config;

import com.brunosong.transfer.system.transfer.service.entity.SourceConfig;
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

@Component
@RequiredArgsConstructor
public class DynamicDataSourceConnector {

    private final MongoConnectHelper mongoConnectHelper;
    private final MysqlConnectHelper mysqlConnectHelper;
//
//    private final Map<SourceType, DataSource> mysqlCache = new ConcurrentHashMap<>();
//    private final Map<SourceType, SourceConfig> lastConfig = new ConcurrentHashMap<>();

    public Optional<Map<String, Object>> query(SourceConfig sourceConfig, String sourceId) {

        return switch (sourceConfig.getDbType()) {
            case MONGO -> mongoConnectHelper.queryMongo(sourceConfig, sourceId);
            case MYSQL -> mysqlConnectHelper.queryMySql(sourceConfig, sourceId);
            case POSTGRESQL -> null;
            case ORACLE -> null;
            case H2 -> null;
        };
    }


//    private DataSource getOrCreateDataSource(SourceType sourceType, DbConfig newConfig) {
//        DbConfig oldConfig = lastConfig.get(sourceType);
//        if (oldConfig == null || !oldConfig.equals(newConfig)) {
//            DataSource oldDs = mysqlCache.remove(sourceType);
//            if (oldDs instanceof HikariDataSource) ((HikariDataSource) oldDs).close();
//            DataSource newDs = createDynamicDataSource(newConfig);
//            mysqlCache.put(sourceType, newDs);
//            lastConfig.put(sourceType, newConfig);
//            return newDs;
//        }
//        return mysqlCache.get(sourceType);
//    }



}
