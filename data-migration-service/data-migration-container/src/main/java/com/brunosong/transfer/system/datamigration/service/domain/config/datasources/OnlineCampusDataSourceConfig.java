package com.brunosong.transfer.system.datamigration.service.domain.config.datasources;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus",
        entityManagerFactoryRef = "onlineCampusEntityManagerFactory",
        transactionManagerRef = "onlineCampusTransactionManager"
)
public class OnlineCampusDataSourceConfig {

    @Bean(name = "onlineCampusProdDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.brunosong-online-campus.prod")
    public DataSource onlineCampusProdDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "onlineCampusDevDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.brunosong-online-campus.dev")
    public DataSource onlineCampusDevDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "onlineCampusRoutingDataSource")
    public DataSource onlineCampusRoutingDataSource(
            @Qualifier("onlineCampusProdDataSource") DataSource onlineCampusProdDataSource,
            @Qualifier("onlineCampusDevDataSource") DataSource onlineCampusDevDataSource) {

        AbstractRoutingDataSource onlineCampusRoutingDataSources = new AbstractRoutingDataSource() {
            @Override
            protected Object determineCurrentLookupKey() {
                return DataSourceContextHolder.getDataSourceKey();
            }
        };

        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("onlineCampusProd", onlineCampusProdDataSource);
        targetDataSources.put("onlineCampusDev", onlineCampusDevDataSource);

        onlineCampusRoutingDataSources.setTargetDataSources(targetDataSources);
        onlineCampusRoutingDataSources.setDefaultTargetDataSource(onlineCampusDevDataSource);
        return onlineCampusDevDataSource;
    }

    @Bean(name = "onlineCampusEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean onlineCampusProdEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("onlineCampusRoutingDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus")
                .persistenceUnit("default")
                .properties(jpaProperties())
                .build();
    }

    @Bean(name = "onlineCampusTransactionManager")
    public PlatformTransactionManager onlineCampusTransactionManager(
            @Qualifier("onlineCampusEntityManagerFactory") LocalContainerEntityManagerFactoryBean factory) {
        return new JpaTransactionManager(factory.getObject());
    }

    private java.util.Map<String, Object> jpaProperties() {
        java.util.Map<String, Object> props = new java.util.HashMap<>();
        props.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        props.put("hibernate.format_sql", true);
        props.put("hibernate.show_sql", true);
        props.put("hibernate.generate_statistics", true);
        props.put("hibernate.hbm2ddl.auto", "update"); // H2용, 테이블 자동 생성
        return props;
    }
}
