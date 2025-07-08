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
import java.util.concurrent.ConcurrentHashMap;

// MySQL 연결을 위한 헬퍼 클래스
@Component
@RequiredArgsConstructor
public class MysqlConnectHelper {

    private final Map<String, DataSource> mysqlCache = new ConcurrentHashMap<>();

    public Optional<Map<String, Object>> queryMySql(SourceConfig sourceConfig, String id) {

        String cacheKey = generateCacheKey(sourceConfig);
        DataSource dataSource = Optional.ofNullable(mysqlCache.get(cacheKey))
                .orElseGet(() -> {
                    DataSource newDataSource = createDynamicDataSource(sourceConfig);
                    mysqlCache.put(cacheKey,newDataSource);
                    return newDataSource;
                });

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

    public DataSource createDynamicDataSource(SourceConfig sourceConfig) {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:%s://%s:%d/%s".formatted(sourceConfig.getDbType().getName(), sourceConfig.getHost(), sourceConfig.getPort(), sourceConfig.getDatabase()));
        ds.setUsername(sourceConfig.getUsername()); // 실제로는 설정에서 동적으로
        ds.setPassword(sourceConfig.getPassword());
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        return ds;
    }

    private String generateCacheKey(SourceConfig sourceConfig) {
        return "datasource:" + sourceConfig.getDbType() + ":" + sourceConfig.getHost() + ":" +
                sourceConfig.getPort() + ":" + sourceConfig.getDatabase();
    }

}
