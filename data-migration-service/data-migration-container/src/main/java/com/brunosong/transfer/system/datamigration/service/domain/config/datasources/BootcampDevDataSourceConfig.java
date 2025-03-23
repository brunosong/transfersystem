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
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.brunosong.transfer.system.datamigration.service.dataaccess.bootcamp",
        entityManagerFactoryRef = "bootcampDevEntityManagerFactory",
        transactionManagerRef = "bootcampDevTransactionManager"
)
public class BootcampDevDataSourceConfig {

    @Bean(name = "bootcampDevDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.brunosong_bootcamp.dev")
    public DataSource bootcampDevDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "bootcampDevEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean bootcampDevEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("bootcampDevDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.brunosong.transfer.system.datamigration.service.dataaccess.bootcamp")
                .persistenceUnit("bootcampDev")
                .properties(jpaProperties())
                .build();
    }

    @Bean(name = "bootcampDevTransactionManager")
    public PlatformTransactionManager bootcampDevTransactionManager(
            @Qualifier("bootcampDevEntityManagerFactory") LocalContainerEntityManagerFactoryBean factory) {
        return new JpaTransactionManager(factory.getObject());
    }

    private Map<String, Object> jpaProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        props.put("hibernate.format_sql", true);
        props.put("hibernate.show_sql", true);
        props.put("hibernate.generate_statistics", true);
        return props;
    }
}
