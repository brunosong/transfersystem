package com.brunosong.transfer.system.transfer.service.dataaccess.config;

import com.brunosong.transfer.system.transfer.service.entity.SourceConfig;
import com.brunosong.transfer.system.transfer.service.valueobject.DbType;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
                     "CREATE TABLE IF NOT EXISTS users (id BIGINT PRIMARY KEY, name VARCHAR(255))")) {
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

        // 딱 한번만 호출됨
        Mockito.verify(mysqlConnectHelper, Mockito.times(1)).createDynamicDataSource(any(SourceConfig.class));

    }


}