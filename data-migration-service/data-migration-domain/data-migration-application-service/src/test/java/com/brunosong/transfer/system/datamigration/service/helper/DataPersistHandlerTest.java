package com.brunosong.transfer.system.datamigration.service.helper;

import com.brunosong.transfer.system.datamigration.service.domain.entity.DataMigration;
import com.brunosong.transfer.system.datamigration.service.handler.DataPersistHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringJUnitConfig(
        classes = {
                BootCampDataPersistHelper.class,
                OnlineCampusDataPersistHelper.class,
                DataPersistHandler.class
        })
class DataPersistHandlerTest {

    @Autowired
    DataPersistHandler dataPersistHandler;

    @MockBean
    OnlineCampusDataPersistHelper onlineCampusDataPersistHelper;

    @Test
    @DisplayName("TargetType이 OnlineCampus인 경우, dataPersist 메서드가 호출되어야 한다.")
    void testDataPersist() {
        // Given
        DataMigration dataMigration = DataMigration.builder()
                .targetSystem("BRUNOSONG_ONLINE_CAMPUS")
                .targetSystemEnvironment(null) // Assuming null is acceptable for this test
                .build();
        // When
        Mockito.when(onlineCampusDataPersistHelper.dataPersist(Mockito.any(), Mockito.any()))
                .thenReturn(100L);

        long result = dataPersistHandler.persist(dataMigration, null);

        // Then
        assertEquals(100L, result);

        Mockito.verify(onlineCampusDataPersistHelper, Mockito.times(1))
                .dataPersist(Mockito.any(), Mockito.any());
    }

  
}