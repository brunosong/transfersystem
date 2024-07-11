package com.brunosong.transfersystem.config.datasources;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class AiServiceDataSourceConfig {

    @Bean("aiServiceRealDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.aiservice-real")
    public DataSource aiServiceRealDataSource() throws IllegalArgumentException {
        return DataSourceBuilder.create().build();
    }

    @Bean("aiServiceDevDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.aiservice-dev")
    public DataSource aiServiceDevDataSource() throws IllegalArgumentException {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "aiServiceDynamicDataSource")
    public DataSource aiServiceDynamicDataSource(@Qualifier("aiServiceRealDataSource") DataSource aiServiceRealDataSource,
                                                 @Qualifier("aiServiceDevDataSource") DataSource aiServiceDevDataSource) {

        RoutingDataSource routingDataSource = new RoutingDataSource();

        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put(DataSourceType.AISERVICE_REAL, aiServiceRealDataSource);
        dataSourceMap.put(DataSourceType.AISERVICE_DEV, aiServiceDevDataSource);

        routingDataSource.setTargetDataSources(dataSourceMap);
        routingDataSource.setDefaultTargetDataSource(aiServiceDevDataSource);

        return routingDataSource;
    }



}
