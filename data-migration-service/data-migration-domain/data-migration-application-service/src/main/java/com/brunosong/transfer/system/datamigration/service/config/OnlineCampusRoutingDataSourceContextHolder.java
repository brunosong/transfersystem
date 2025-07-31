package com.brunosong.transfer.system.datamigration.service.config;

import com.brunosong.transfer.system.datamigration.service.domain.valueobject.DataSourceType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OnlineCampusRoutingDataSourceContextHolder {

    private static final ThreadLocal<DataSourceType> contextHolder = new ThreadLocal<>();

    public static void setDataSourceType(DataSourceType dataSourceType) {
        log.info("OnlineCampus setupDataSourceType is {}", dataSourceType.name());
        contextHolder.set(dataSourceType);
    }

    public static void clearDataSourceType() {
        log.info("OnlineCampus clearDataSourceType is {}", contextHolder.getClass());
        contextHolder.remove();
    }

    public static DataSourceType getDataSourceType() {
        return contextHolder.get();
    }
}
