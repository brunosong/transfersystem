package com.brunosong.transfer.system.aiservice.config.aspect;

import com.brunosong.transfer.system.aiservice.config.datasources.RoutingDataSource;
import com.brunosong.transfer.system.aiservice.config.datasources.DataSourceType;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AiServiceDataSourceAspect {

    @Before("@annotation(com.brunosong.transfer.system.aiservice.config.annotation.UseAiServiceRealDataSource)")
    public void setPrimaryDataSource() {
        RoutingDataSource.setDataSourceType(DataSourceType.AISERVICE_REAL);
    }

    @Before("@annotation(com.brunosong.transfer.system.aiservice.config.annotation.UseAiServiceDevDataSource)")
    public void setSecondaryDataSource() {
        RoutingDataSource.setDataSourceType(DataSourceType.AISERVICE_DEV);
    }

    @After("@annotation(com.brunosong.transfer.system.aiservice.config.annotation.UseAiServiceRealDataSource) || @annotation(com.brunosong.transfer.system.aiservice.config.annotation.UseAiServiceDevDataSource)")
    public void clearDataSource() {
        RoutingDataSource.clearDataSourceType();
    }

}
