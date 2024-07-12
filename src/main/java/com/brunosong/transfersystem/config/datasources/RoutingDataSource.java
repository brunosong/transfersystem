package com.brunosong.transfersystem.config.datasources;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

@Slf4j
public class RoutingDataSource extends AbstractRoutingDataSource {

    private static final ThreadLocal<DataSourceType> contextHolder = new ThreadLocal<>();

    public static void setDataSourceType(DataSourceType dataSourceType) {
        log.info("AI_SERVICE DATASOURCE SETTING IS {}" , dataSourceType.name());
        contextHolder.set(dataSourceType);
    }

    public static void clearDataSourceType() {
        log.info("AI_SERVICE clearDataSourceType IS {}" , contextHolder.getClass());
        contextHolder.remove();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return contextHolder.get();
    }

}
