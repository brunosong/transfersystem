package com.brunosong.transfersystem.config.aspect;

import com.brunosong.transfersystem.config.datasources.DataSourceType;
import com.brunosong.transfersystem.config.datasources.RoutingDataSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class AiServiceDataSourceAspectTest {

    @InjectMocks
    private AiServiceDataSourceAspect aspect;

    /* 이 테스트가 무슨 의미가 있지? */
    @Test
    void setPrimaryDataSource_가_실행되면_setDataSourceType_가_실행된다() {
        try (MockedStatic mockStatic = mockStatic(RoutingDataSource.class)) {
            aspect.setPrimaryDataSource();
            mockStatic.verify(() -> RoutingDataSource.setDataSourceType(any()) , times(1));
        }
    }


    @Test
    void setSecondaryDataSource_가_실행되면_setDataSourceType_가_실행된다() {
        try (MockedStatic mockStatic = mockStatic(RoutingDataSource.class)) {
            aspect.setSecondaryDataSource();
            mockStatic.verify(() -> RoutingDataSource.setDataSourceType(any()) , times(1));
        }
    }


    @Test
    void clearDataSource_가_실행되면_clearDataSourceType_이_실행된다() {
        try (MockedStatic mockStatic = mockStatic(RoutingDataSource.class)) {
            aspect.clearDataSource();
            mockStatic.verify(() -> RoutingDataSource.clearDataSourceType() , times(1));
        }
    }
    

}