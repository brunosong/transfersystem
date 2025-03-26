package com.brunosong.transfer.system.datamigration.service.domain.config.datasources;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
        basePackages = "com.brunosong.transfer.system.datamigration.service.dataaccess.bootcamp",
        entityManagerFactoryRef = "bootcampEntityManagerFactory",
        transactionManagerRef = "bootcampTransactionManager"
)
public class BootcampProdDataSourceConfig {

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

        AbstractRoutingDataSource bootcampRoutingDataSource = new AbstractRoutingDataSource() {
            @Override
            protected Object determineCurrentLookupKey() {
                return DataSourceContextHolder.getDataSourceKey();
            }
        };

        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("bootcampProd", bootcampProdDataSource);
        targetDataSources.put("bootcampDev", bootcampDevDataSource);

        bootcampRoutingDataSource.setTargetDataSources(targetDataSources);
        bootcampRoutingDataSource.setDefaultTargetDataSource(bootcampDevDataSource);
        return bootcampRoutingDataSource;
    }

    @Bean(name = "bootcampEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean bootcampEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("bootcampProdDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.brunosong.transfer.system.datamigration.service.dataaccess.bootcamp")
                .persistenceUnit("bootcamp")
                .properties(jpaProperties())
                .build();
    }

    @Bean(name = "bootcampTransactionManager")
    public PlatformTransactionManager bootcampTransactionManager(
            @Qualifier("bootcampEntityManagerFactory") LocalContainerEntityManagerFactoryBean factory) {
        return new JpaTransactionManager(factory.getObject());
    }

    private Map<String, Object> jpaProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        props.put("hibernate.format_sql", true);
        props.put("hibernate.show_sql", true);
        props.put("hibernate.generate_statistics", true);
        props.put("hibernate.hbm2ddl.auto", "update"); // H2용, 테이블 자동 생성
        return props;
    }
}