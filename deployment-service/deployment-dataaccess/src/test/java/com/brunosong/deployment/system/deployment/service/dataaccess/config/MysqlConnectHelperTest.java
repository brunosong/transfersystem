package com.brunosong.deployment.system.deployment.service.dataaccess.config;

import com.brunosong.deployment.system.deployment.service.entity.SourceConfig;
import com.brunosong.deployment.system.deployment.service.valueobject.DbType;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class MysqlConnectHelperTest {

    @Spy
    @InjectMocks
    MysqlConnectHelper mysqlConnectHelper;

    private HikariDataSource dataSource;

    @BeforeEach
    public void setUp() throws Exception {
        // H2 설정
        dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        dataSource.setMaximumPoolSize(10);
        dataSource.setMinimumIdle(5);

        // 테이블 및 데이터 초기화
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "CREATE TABLE IF NOT EXISTS users (id BIGINT, name VARCHAR(255))")) {
            stmt.execute();
        }
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO users (id, name) VALUES (?, ?)")) {
            stmt.setLong(1, 1L);
            stmt.setString(2, "John Doe");
            stmt.executeUpdate();
        }
    }

    @AfterEach
    public void tearDown() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    @Test
    @DisplayName("MySQL 원본 테이블에서 id로 행 하나를 찾으면 칸 이름과 값이 담긴 Map으로 돌려준다")
    void queryMySql() {
        SourceConfig sourceConfig = SourceConfig.builder()
                .tableOrCollection("users")
                .build();

        // createDynamicDataSource 모킹
        Mockito.doReturn(dataSource).when(mysqlConnectHelper).createDynamicDataSource(Mockito.any(SourceConfig.class));

        // When: queryMySql 호출
        Optional<Map<String, Object>> result = mysqlConnectHelper.queryMySql(sourceConfig, "1");

        // Then: 결과 검증
        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().get("NAME"));
        assertEquals(1L, result.get().get("ID"));

    }

    @Test
    @DisplayName("같은 원본 설정으로 두 번 조회하면 연결 풀은 한 번만 만들고, 두 번째 조회도 닫히지 않은 그 풀로 성공한다")
    void queryMySqlCacheHit() {
        SourceConfig sourceConfig = SourceConfig.builder()
                .dbType(DbType.H2)
                .host("localhost")
                .port(8088)
                .database("testdb")
                .tableOrCollection("users")
                .build();

        // createDynamicDataSource 모킹
        Mockito.doReturn(dataSource).when(mysqlConnectHelper).createDynamicDataSource(Mockito.any(SourceConfig.class));

        // When: queryMySql 호출
        Optional<Map<String, Object>> result = mysqlConnectHelper.queryMySql(sourceConfig, "1");

        // Then: 결과 검증
        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().get("NAME"));
        assertEquals(1L, result.get().get("ID"));

        // 두 번째 호출은 캐시의 풀을 다시 쓴다. 닫힌 풀이면 여기서 실패한다
        Optional<Map<String, Object>> second = mysqlConnectHelper.queryMySql(sourceConfig, "1");
        assertTrue(second.isPresent());

        // 딱 한번만 호출됨
        Mockito.verify(mysqlConnectHelper, Mockito.times(1)).createDynamicDataSource(any(SourceConfig.class));

    }


}