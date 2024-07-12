package com.brunosong.transfersystem;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/*
*  멀티데이터소스를 사용하기에 설정파일을 사용해서 스크립트를 집어넣기에는 한계가 존재함
*  명확하게 하기 위해 DDL은 JPA에게 맡기고 DML은 data.sql로 작성하여 insert
*/

@Component
public class DataSourceInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private final DataSource mainDataSource;

    private final DataSource aiServiceRealDataSource;

    private final DataSource aiServiceDevDataSource;

    public DataSourceInitializer(@Qualifier("mainDataSource") DataSource mainDataSource,
                                 @Qualifier("aiServiceRealDataSource") DataSource aiServiceRealDataSource,
                                 @Qualifier("aiServiceDevDataSource") DataSource aiServiceDevDataSource) {

        this.mainDataSource = mainDataSource;
        this.aiServiceRealDataSource = aiServiceRealDataSource;
        this.aiServiceDevDataSource = aiServiceDevDataSource;

    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        initializeDataSource(mainDataSource, "db/main/data.sql");
//        initializeDataSource(aiServiceRealDataSource, "classpath:db/aiservice/real/data.sql");
//        initializeDataSource(aiServiceDevDataSource, "classpath:db/aiservice/dev/data.sql");
    }

    private void initializeDataSource(DataSource dataSource, String scriptLocation) {
        Resource resource = new ClassPathResource(scriptLocation);
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, resource);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize data source with script: " + scriptLocation, e);
        }
    }

}
