package com.brunosong.transfersystem.mock;

import com.brunosong.transfersystem.config.datasources.DataSourceType;
import com.brunosong.transfersystem.config.datasources.RoutingDataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;


@TestConfiguration
public class TestDataSourceConfig {

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
