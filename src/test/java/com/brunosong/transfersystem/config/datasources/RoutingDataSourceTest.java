package com.brunosong.transfersystem.config.datasources;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RoutingDataSourceTest.DataSourceConfig.class)
class RoutingDataSourceTest {

    @Autowired
    DataSource dataSource;

    @Test
    public void RoutingDataSource가_REAL_DATABASE를_바라본다() throws Exception {
        RoutingDataSource.setDataSourceType(DataSourceType.AISERVICE_REAL);
        try (Connection connection = dataSource.getConnection()) {
            Assertions.assertThat("REAL").isEqualTo(connection.getCatalog().toUpperCase());
        }
    }

    @Test
    public void RoutingDataSource가_DEV_DATABASE를_바라본다() throws Exception {
        RoutingDataSource.setDataSourceType(DataSourceType.AISERVICE_DEV);
        try (Connection connection = dataSource.getConnection()) {
            Assertions.assertThat("DEV").isEqualTo(connection.getCatalog().toUpperCase());
        }
    }

    @TestConfiguration
    public static class DataSourceConfig {

        @Bean
        public DataSource dataSource() {
            Map<Object, Object> dataSourceMap = new HashMap<>();
            dataSourceMap.put(DataSourceType.AISERVICE_REAL, realDataSource());
            dataSourceMap.put(DataSourceType.AISERVICE_DEV, devDataSource());

            RoutingDataSource routingDataSource = new RoutingDataSource();
            routingDataSource.setTargetDataSources(dataSourceMap);
            routingDataSource.setDefaultTargetDataSource(devDataSource());

            return routingDataSource;
        }

        @Bean
        public DataSource realDataSource() {
            return DataSourceBuilder.create()
                    .url("jdbc:h2:mem:real")
                    .username("sa")
                    .password("")
                    .driverClassName("org.h2.Driver")
                    .build();
        }

        @Bean
        public DataSource devDataSource() {
            return DataSourceBuilder.create()
                    .url("jdbc:h2:mem:dev")
                    .username("sa")
                    .password("")
                    .driverClassName("org.h2.Driver")
                    .build();
        }

    }
}