package com.brunosong.transfer.system.datamigration.service.domain.config.datasources;

public class DataSourceContextHolder {
    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

    public static void setDataSourceKey(String key) {
        contextHolder.set(key);
    }

    public static String getDataSourceKey() {
        return contextHolder.get();
    }

    public static void clear() {
        contextHolder.remove();
    }
}
