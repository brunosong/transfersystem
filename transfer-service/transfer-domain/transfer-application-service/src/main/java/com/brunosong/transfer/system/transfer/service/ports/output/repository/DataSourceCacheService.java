package com.brunosong.transfer.system.transfer.service.ports.output.repository;

import javax.sql.DataSource;
import java.util.Optional;

public interface DataSourceCacheService {
    Optional<DataSource> findDataSource(String cacheKey);
    void saveDataSource(String cacheKey, DataSource dataSource);
}
