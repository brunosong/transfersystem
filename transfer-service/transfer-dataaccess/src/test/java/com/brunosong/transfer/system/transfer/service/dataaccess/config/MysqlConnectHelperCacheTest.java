package com.brunosong.transfer.system.transfer.service.dataaccess.config;

import com.brunosong.transfer.system.transfer.service.dto.event.DataSourceCacheEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(classes = {MysqlConnectHelper.class})
class MysqlConnectHelperCacheTest {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @SpyBean
    private MysqlConnectHelper mysqlConnectHelper;


    @Test
    @DisplayName("DataSourceCacheEvent가 발생하면 MysqlConnectHelper가 이벤트를 처리한다.")
    void testMysqlConnectHelperCache() {
        DataSourceCacheEvent dataSourceCacheEvent = new DataSourceCacheEvent(this, DataSourceCacheEvent.EventType.DATA_SOURCE_CACHE_DELETE);

        eventPublisher.publishEvent(dataSourceCacheEvent);

        Mockito.verify(mysqlConnectHelper, Mockito.times(1)).handleDataDeletedEvent(dataSourceCacheEvent);
    }


}