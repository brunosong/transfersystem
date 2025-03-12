package com.brunosong.transfer.system.transfer.service.dataaccess.config;

import com.brunosong.transfer.system.transfer.service.valueobject.DbType;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceType;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DbConfigManager {

    private final Map<SourceType, DbConfig> configMap = new ConcurrentHashMap<>();

    public void setDbConfig(SourceType sourceType, DbType dbType, String host, int port, String database, String username,
                            String password, String tableOrCollection ) {
        configMap.put(sourceType, new DbConfig(dbType, host, port, database, username, password, tableOrCollection));
    }

    public DbConfig getDbConfig(SourceType sourceType) {
        DbConfig config = configMap.get(sourceType);
        if (config == null) {
            throw new IllegalStateException("No DB config found for source type: " + sourceType);
        }
        return config;
    }
}
