package com.brunosong.transfer.system.transfer.service.dataaccess.config;

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

    private final DbConfigManager dbConfigManager;
    private final Map<SourceType, DataSource> mysqlCache = new ConcurrentHashMap<>();
    private final Map<SourceType, DbConfig> lastConfig = new ConcurrentHashMap<>();

    public Optional<Map<String, Object>> query(SourceType sourceType, String id) {
        DbConfig config = dbConfigManager.getDbConfig(sourceType);

        return switch (config.getDbType()) {
            case MONGO -> queryMongo(config, id);
            case MYSQL -> queryMySql(config, id);
        };
    }

    public  List<Document> queryList(SourceType sourceType) {
        DbConfig config = dbConfigManager.getDbConfig(sourceType);

        return queryMongoList(config);
    }

    private List<Document> queryMongoList(DbConfig config) {
        try (MongoClient mongoClient = MongoClients.create(
                "mongodb://" + config.getHost() + ":" + config.getPort())) {
            MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, config.getDatabase());

            List<Document> all = mongoTemplate.findAll(Document.class, config.getTableOrCollection());
            return all;
        }
    }

    private Optional<Map<String, Object>> queryMongo(DbConfig config, String id) {
        try (MongoClient mongoClient = MongoClients.create(
                "mongodb://" + config.getHost() + ":" + config.getPort())) {
            MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, config.getDatabase());

            Query query = new Query(Criteria.where("_id").is(id));
            Document doc = mongoTemplate.findOne(query, Document.class, config.getTableOrCollection());
            return Optional.ofNullable(doc).map(d -> (Map<String, Object>) d);
        }
    }

    private Optional<Map<String, Object>> queryMySql(DbConfig config, String id) {
        DataSource dataSource = createDynamicDataSource(config);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        String sql = "SELECT * FROM " + config.getTableOrCollection() + " WHERE id = ?";
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

    private DataSource getOrCreateDataSource(SourceType sourceType, DbConfig newConfig) {
        DbConfig oldConfig = lastConfig.get(sourceType);
        if (oldConfig == null || !oldConfig.equals(newConfig)) {
            DataSource oldDs = mysqlCache.remove(sourceType);
            if (oldDs instanceof HikariDataSource) ((HikariDataSource) oldDs).close();
            DataSource newDs = createDynamicDataSource(newConfig);
            mysqlCache.put(sourceType, newDs);
            lastConfig.put(sourceType, newConfig);
            return newDs;
        }
        return mysqlCache.get(sourceType);
    }

    private DataSource createDynamicDataSource(DbConfig config) {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:mysql://" + config.getHost() + ":" + config.getPort() + "/" + config.getDatabase());
        ds.setUsername("root"); // 실제로는 설정에서 동적으로
        ds.setPassword("password");
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        return ds;
    }


}
