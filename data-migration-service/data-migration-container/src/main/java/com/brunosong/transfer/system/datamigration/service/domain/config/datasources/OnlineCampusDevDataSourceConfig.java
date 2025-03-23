package com.brunosong.transfer.system.datamigration.service.domain.config.datasources;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus",
        entityManagerFactoryRef = "onlineCampusDevEntityManagerFactory",
        transactionManagerRef = "onlineCampusDevTransactionManager"
)
public class OnlineCampusDevDataSourceConfig {

    @Bean(name = "onlineCampusDevDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.brunosong_online_campus.dev")
    public DataSource onlineCampusDevDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "onlineCampusDevEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean onlineCampusDevEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("onlineCampusDevDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus")
                .persistenceUnit("onlineCampusDev")
                .properties(jpaProperties())
                .build();
    }

    @Bean(name = "onlineCampusDevTransactionManager")
    public PlatformTransactionManager onlineCampusDevTransactionManager(
            @Qualifier("onlineCampusDevEntityManagerFactory") LocalContainerEntityManagerFactoryBean factory) {
        return new JpaTransactionManager(factory.getObject());
    }

    private java.util.Map<String, Object> jpaProperties() {
        java.util.Map<String, Object> props = new java.util.HashMap<>();
        props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        props.put("hibernate.format_sql", true);
        props.put("hibernate.show_sql", true);
        props.put("hibernate.generate_statistics", true);
        return props;
    }
}
