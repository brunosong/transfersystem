package com.brunosong.transfersystem.config.datasources;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

@Slf4j
public class RoutingDataSource extends AbstractRoutingDataSource {

    private static final ThreadLocal<DataSourceType> CONTEXT = new ThreadLocal<>();

    public static void setDataSourceType(DataSourceType dataSourceType) {
        log.info("AI_SERVICE DATASOURCE SETTING IS {}" , dataSourceType.name());
        CONTEXT.set(dataSourceType);
    }

    public static void clearDataSourceType() {
        CONTEXT.remove();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return CONTEXT.get();
    }

}
