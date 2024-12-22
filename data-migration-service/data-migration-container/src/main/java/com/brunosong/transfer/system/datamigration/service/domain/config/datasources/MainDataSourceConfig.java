package com.brunosong.transfer.system.datamigration.service.domain.config.datasources;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class MainDataSourceConfig {

    /* spring.datasource에 바로 지정해서 dataSource()를 사용해도 가능하지만 명확환 예제를 위해 mainDataSource() 생성 */
    @Bean("mainDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.main")
    public DataSource mainDataSource() throws IllegalArgumentException {
        return DataSourceBuilder.create().build();
    }


}
