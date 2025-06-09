package com.brunosong.transfer.system.datamigration.service.config.datasources;

import org.springframework.context.annotation.*;

@Configuration
public class DataMigrationDataSourceConfig {

//    @DependsOn({"bootcampDevDataSource", "bootcampProdDataSource","onlineCampusDevDataSource", "onlineCampusProdDataSource"})
//    @Bean(name = "dataMigrationDynamicDataSource")
//    public DataSource dataMigrationDynamicDataSource(@Qualifier("bootcampDevDataSource") DataSource bootcampDevDataSource,
//                                                     @Qualifier("bootcampProdDataSource") DataSource bootcampProdDataSource,
//                                                     @Qualifier("onlineCampusDevDataSource") DataSource onlineCampusDevDataSource,
//                                                     @Qualifier("onlineCampusProdDataSource") DataSource onlineCampusProdDataSource) {
//
//        RoutingDataSourceContextHolder routingDataSourceContextHolder = new RoutingDataSourceContextHolder();
//
//        Map<Object, Object> dataSourceMap = new HashMap<>();
//        dataSourceMap.put(DataSourceType.BRUNOSONG_ONLINE_CAMPUS_PROD, onlineCampusProdDataSource);
//        dataSourceMap.put(DataSourceType.BRUNOSONG_ONLINE_CAMPUS_DEV, onlineCampusDevDataSource);
//        dataSourceMap.put(DataSourceType.BRUNOSONG_BOOTCAMP_PROD, bootcampProdDataSource);
//        dataSourceMap.put(DataSourceType.BRUNOSONG_BOOTCAMP_DEV, bootcampDevDataSource);
//
//        routingDataSourceContextHolder.setTargetDataSources(dataSourceMap);
//        routingDataSourceContextHolder.setDefaultTargetDataSource(bootcampDevDataSource);
//
//        return routingDataSourceContextHolder;
//    }
//
//    @DependsOn({"dataMigrationDynamicDataSource"})
//    @Bean("dataMigrationDataSource")
//    public LazyConnectionDataSourceProxy dataMigrationDataSource(@Qualifier("dataMigrationDynamicDataSource") DataSource dataMigrationDynamicDataSource) {
//        return new LazyConnectionDataSourceProxy(dataMigrationDynamicDataSource);
//    }

}
