package com.brunosong.transfer.system.transfer.service.cache.transfer.adapter;

import com.brunosong.transfer.system.transfer.service.ports.output.repository.DataSourceCacheService;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MapDataSourceCacheAdapter implements DataSourceCacheService {

    private final ConcurrentHashMap<String, DataSource> cache = new ConcurrentHashMap<>();

    @Override
    public Optional<DataSource> findDataSource(String cacheKey) {
        return Optional.ofNullable(cache.get(cacheKey));
    }

    @Override
    public void saveDataSource(String cacheKey, DataSource dataSource) {
        cache.put(cacheKey, dataSource);
    }
}
