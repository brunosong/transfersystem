package com.brunosong.deployment.system.deployment.service.dataaccess.config;

import com.brunosong.deployment.system.deployment.service.dto.event.DataSourceCacheEvent;
import com.brunosong.deployment.system.deployment.service.entity.SourceConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
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
@Slf4j
@Component
@RequiredArgsConstructor
public class MysqlConnectHelper {

    private final Map<String, HikariDataSource> mysqlCache = new ConcurrentHashMap<>();

    /** 원본 설정이 바뀌었다는 이벤트를 받으면 풀을 닫고 캐시를 비운다. 맵만 새로 만들면 옛 풀의 연결이 남는다 */
    @EventListener
    public void handleDataDeletedEvent(DataSourceCacheEvent event) {
        if (event.getEventType() == DataSourceCacheEvent.EventType.DATA_SOURCE_CACHE_DELETE) {
            closeAll();
            log.info("MySQL DataSource cache cleared due to event: {}", event.getEventType());
        }
    }

    /** 원본 테이블에서 id로 행 하나를 찾아 칸 이름과 값의 Map으로 돌려준다. 풀은 캐시에서 꺼내 다시 쓴다 */
    public Optional<Map<String, Object>> queryMySql(SourceConfig sourceConfig, String id) {

        DataSource dataSource = mysqlCache.computeIfAbsent(generateCacheKey(sourceConfig),
                key -> createDynamicDataSource(sourceConfig));

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        String sql = "SELECT * FROM " + sourceConfig.getTableOrCollection() + " WHERE id = ?";
        List<Map<String, Object>> results = jdbcTemplate.query(sql, (rs, rowNum) -> {
                Map<String, Object> result = new HashMap<>();
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = rs.getObject(i);
                    result.put(columnName, value);
                }
                return result;
            }, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    /** 원본 설정으로 연결 풀을 만든다. 테스트에서 가짜 풀로 바꿔 끼우려고 따로 뺐다 */
    public HikariDataSource createDynamicDataSource(SourceConfig sourceConfig) {
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

    /** 캐시의 풀을 모두 닫고 비운다. 앱이 내려갈 때도 불린다 */
    @PreDestroy
    public void closeAll() {
        mysqlCache.values().forEach(HikariDataSource::close);
        mysqlCache.clear();
    }

}
