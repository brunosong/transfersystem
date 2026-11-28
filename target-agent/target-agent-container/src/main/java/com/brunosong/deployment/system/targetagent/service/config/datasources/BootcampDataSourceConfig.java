package com.brunosong.deployment.system.targetagent.service.config.datasources;

import com.brunosong.deployment.system.targetagent.service.config.BootcampRoutingDataSourceContextHolder;
import com.brunosong.deployment.system.targetagent.service.domain.valueobject.DataSourceType;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 부트캠프 대상 DB. 온라인캠퍼스와 달리 JPA 를 쓰지 않고 JdbcTemplate 으로 납작한 표에 쓴다.
 * 트리를 엔티티로 병합할 필요가 없어서다(과목 청크는 그 과목의 차시를 지우고 다시 넣는다)
 */
@Configuration
public class BootcampDataSourceConfig {

    @Bean(name = "bootcampProdDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.brunosong-bootcamp.prod")
    public DataSource bootcampProdDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "bootcampDevDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.brunosong-bootcamp.dev")
    public DataSource bootcampDevDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "bootcampRoutingDataSource")
    public DataSource bootcampRoutingDataSource(
            @Qualifier("bootcampProdDataSource") DataSource bootcampProdDataSource,
            @Qualifier("bootcampDevDataSource") DataSource bootcampDevDataSource) {

        // 대상 DB마다 표를 만든다. 라우팅 DataSource 로 한 번만 돌리면 기본 DB(DEV)에만 생긴다
        migrate(bootcampProdDataSource);
        migrate(bootcampDevDataSource);

        AbstractRoutingDataSource bootcampRoutingDataSource = new AbstractRoutingDataSource() {
            @Override
            protected Object determineCurrentLookupKey() {
                return BootcampRoutingDataSourceContextHolder.getDataSourceType();
            }
        };

        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DataSourceType.BRUNOSONG_BOOTCAMP_PROD, bootcampProdDataSource);
        targetDataSources.put(DataSourceType.BRUNOSONG_BOOTCAMP_DEV, bootcampDevDataSource);

        bootcampRoutingDataSource.setTargetDataSources(targetDataSources);
        bootcampRoutingDataSource.setDefaultTargetDataSource(bootcampDevDataSource);
        return bootcampRoutingDataSource;
    }

    @Bean(name = "bootcampJdbcTemplate")
    public JdbcTemplate bootcampJdbcTemplate(@Qualifier("bootcampRoutingDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    // 트랜잭션을 시작할 때 커넥션을 잡으므로, 그 전에 라우팅 키(PROD/DEV)가 정해져 있어야 한다
    @Bean(name = "bootcampTransactionManager")
    public PlatformTransactionManager bootcampTransactionManager(
            @Qualifier("bootcampRoutingDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    private void migrate(DataSource dataSource) {
        Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/target/bootcamp")
                .load()
                .migrate();
    }
}
