package com.brunosong.transfersystem.config.jpa;

import com.brunosong.transfersystem.config.properties.AiServiceJpaProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@EnableJpaRepositories(
        basePackages = "com.brunosong.transfersystem.aiservice.infrastructure",
        entityManagerFactoryRef = "aiServiceEntityManager",
        transactionManagerRef = "aiServiceJpaTransactionManager"
)
@Configuration
public class AiServiceJpaConfig {

    @Bean
    public LocalContainerEntityManagerFactoryBean aiServiceEntityManager(@Qualifier("aiServiceDynamicDataSource") DataSource aiServiceDynamicDataSource,
                                                                         @Qualifier("aiServiceJpaVendorAdapter") JpaVendorAdapter aiServiceJpaVendorAdapter,
                                                                         AiServiceJpaProperties aiServiceJpaProperties ) {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(aiServiceDynamicDataSource);
        em.setPackagesToScan(aiServiceJpaProperties.getScanPackagePath());
        em.setJpaVendorAdapter(aiServiceJpaVendorAdapter);
        em.setPersistenceUnitName("aiServiceEntityManager");
        em.setJpaPropertyMap(aiServiceJpaProperties.getProperties());
        return em;
    }

    @Bean
    public PlatformTransactionManager aiServiceJpaTransactionManager(@Qualifier("aiServiceEntityManager") LocalContainerEntityManagerFactoryBean aiServiceEntityManager) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(aiServiceEntityManager.getObject());
        return transactionManager;
    }

    @Bean
    public JpaVendorAdapter aiServiceJpaVendorAdapter(AiServiceJpaProperties aiServiceJpaProperties) {
        AbstractJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setShowSql(aiServiceJpaProperties.isShowSql());
        jpaVendorAdapter.setDatabasePlatform(aiServiceJpaProperties.getDatabasePlatform());
        jpaVendorAdapter.setGenerateDdl(aiServiceJpaProperties.isGenerateDdl());
        return jpaVendorAdapter;
    }

}
