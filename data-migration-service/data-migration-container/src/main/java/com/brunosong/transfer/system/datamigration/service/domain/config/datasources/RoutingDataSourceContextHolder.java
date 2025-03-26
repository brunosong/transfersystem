package com.brunosong.transfer.system.datamigration.service.domain.config.datasources;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

@Slf4j
public class RoutingDataSourceContextHolder extends AbstractRoutingDataSource {

    private static final ThreadLocal<DataSourceType> contextHolder = new ThreadLocal<>();

    public static void setDataSourceType(DataSourceType dataSourceType) {
        log.info("DATASOURCE SETTING IS {}" , dataSourceType.name());
        contextHolder.set(dataSourceType);
    }

    public static void clearDataSourceType() {
        log.info("clearDataSourceType IS {}" , contextHolder.getClass());
        contextHolder.remove();
    }

    public static DataSourceType getDataSourceType() {
        return contextHolder.get();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return contextHolder.get();
    }

}
