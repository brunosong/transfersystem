package com.brunosong.transfer.system.datamigration.service.domain.config.datasources;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class AiServiceDataSourceConfig {

    @DependsOn({"bootcampDevDataSource", "bootcampProdDataSource","onlineCampusDevDataSource", "onlineCampusProdDataSource"})
    @Bean(name = "dataMigrationDynamicDataSource")
    public DataSource dataMigrationDynamicDataSource(@Qualifier("bootcampDevDataSource") DataSource bootcampDevDataSource,
                                                     @Qualifier("bootcampProdDataSource") DataSource bootcampProdDataSource,
                                                     @Qualifier("onlineCampusDevDataSource") DataSource onlineCampusDevDataSource,
                                                     @Qualifier("onlineCampusProdDataSource") DataSource onlineCampusProdDataSource) {

        RoutingDataSource routingDataSource = new RoutingDataSource();

        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put(DataSourceType.BRUNOSONG_ONLINE_CAMPUS_PROD, onlineCampusProdDataSource);
        dataSourceMap.put(DataSourceType.BRUNOSONG_ONLINE_CAMPUS_DEV, onlineCampusDevDataSource);
        dataSourceMap.put(DataSourceType.BRUNOSONG_BOOTCAMP_PROD, bootcampProdDataSource);
        dataSourceMap.put(DataSourceType.BRUNOSONG_BOOTCAMP_DEV, bootcampDevDataSource);

        routingDataSource.setTargetDataSources(dataSourceMap);
        routingDataSource.setDefaultTargetDataSource(bootcampDevDataSource);

        return routingDataSource;
    }

    @DependsOn({"dataMigrationDynamicDataSource"})
    @Bean("dataMigrationDataSource")
    public LazyConnectionDataSourceProxy dataMigrationDataSource(@Qualifier("dataMigrationDynamicDataSource") DataSource dataMigrationDynamicDataSource) {
        return new LazyConnectionDataSourceProxy(dataMigrationDynamicDataSource);
    }

}
