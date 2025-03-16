package com.brunosong.transfer.system.transfer.service.dataaccess.config;

import com.brunosong.transfer.system.transfer.service.entity.SourceConfig;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceType;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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
//
//    private final Map<SourceType, DataSource> mysqlCache = new ConcurrentHashMap<>();
//    private final Map<SourceType, SourceConfig> lastConfig = new ConcurrentHashMap<>();

    public Optional<Map<String, Object>> query(SourceConfig sourceConfig, String sourceId) {

        return switch (sourceConfig.getDbType()) {
            case MONGO -> queryMongo(sourceConfig, sourceId);
            case MYSQL -> queryMySql(sourceConfig, sourceId);
        };
    }

    public  List<Document> queryList(SourceConfig sourceConfig) {
        return queryMongoList(sourceConfig);
    }

    private List<Document> queryMongoList(SourceConfig sourceConfig) {
        try (MongoClient mongoClient = MongoClients.create(
                "mongodb://" + sourceConfig.getHost() + ":" + sourceConfig.getPort())) {
            MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, sourceConfig.getDatabase());

            List<Document> all = mongoTemplate.findAll(Document.class, sourceConfig.getTableOrCollection());
            return all;
        }
    }

    private Optional<Map<String, Object>> queryMongo(SourceConfig sourceConfig, String sourceId) {
        String mongoUri = String.format(
                "mongodb://%s:%s@%s:%d/%s?authSource=%s",
                sourceConfig.getUsername(),
                sourceConfig.getPassword(),
                sourceConfig.getHost(),
                sourceConfig.getPort(),
                sourceConfig.getDatabase(),
                sourceConfig.getDatabase() // authSource로 database 사용 (필요 시 'admin'으로 변경)
        );

        System.out.println(mongoUri);

        try (MongoClient mongoClient = MongoClients.create(mongoUri)) {
            MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, sourceConfig.getDatabase());

            List<Document> all = mongoTemplate.findAll(Document.class, sourceConfig.getTableOrCollection());

            Query query = new Query(Criteria.where("_id").is(sourceId));
            Document doc = mongoTemplate.findOne(query, Document.class, sourceConfig.getTableOrCollection());
            return Optional.ofNullable(doc).map(d -> (Map<String, Object>) d);
        } catch (Exception e) {
            // 예외 처리 추가
            throw new RuntimeException("Failed to query MongoDB: " + e.getMessage(), e);
        }
    }

    private Optional<Map<String, Object>> queryMySql(SourceConfig sourceConfig, String id) {
        DataSource dataSource = createDynamicDataSource(sourceConfig);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        String sql = "SELECT * FROM " + sourceConfig.getTableOrCollection() + " WHERE id = ?";
        try {
            List<Map<String, Object>> results = jdbcTemplate.query(sql,(rs, rowNum) -> {
                Map<String, Object> result = new HashMap<>();
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = rs.getObject(i);
                    result.put(columnName, value);
                }
                return result;
            }, new Object[]{id} );
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } finally {
            if (dataSource instanceof HikariDataSource) {
                ((HikariDataSource) dataSource).close();
            }
        }
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

    private DataSource createDynamicDataSource(SourceConfig sourceConfig) {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:mysql://" + sourceConfig.getHost() + ":" + sourceConfig.getPort() + "/" + sourceConfig.getDatabase());
        ds.setUsername(sourceConfig.getUsername()); // 실제로는 설정에서 동적으로
        ds.setPassword(sourceConfig.getPassword());
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        return ds;
    }


}
