package com.brunosong.transfer.system.ai.service.application.rest.config.jpa;

import com.brunosong.transfer.system.ai.service.application.rest.config.properties.MainJpaProperties;
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

/* Main 패키지에서는 repository 사용 */
@EnableJpaRepositories(
        basePackages = "com.brunosong.transfersystem.main.repository",
        entityManagerFactoryRef = "mainEntityManager",
        transactionManagerRef = "mainJpaTransactionManager"
)
@Configuration
public class MainJpaConfig {

    @Bean
    public LocalContainerEntityManagerFactoryBean mainEntityManager(@Qualifier("mainDataSource") DataSource mainDataSource,
                                                                    @Qualifier("mainJpaVendorAdapter") JpaVendorAdapter mainJpaVendorAdapter,
                                                                    MainJpaProperties mainJpaProperties) {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(mainDataSource);
        em.setPackagesToScan(mainJpaProperties.getScanPackagePath());
        em.setJpaVendorAdapter(mainJpaVendorAdapter);
        em.setPersistenceUnitName("mainEntityManager");
        em.setJpaPropertyMap(mainJpaProperties.getProperties());
        return em;
    }

    @Bean
    public PlatformTransactionManager mainJpaTransactionManager(@Qualifier("mainEntityManager") LocalContainerEntityManagerFactoryBean mainEntityManager) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(mainEntityManager.getObject());
        return transactionManager;
    }

    @Bean
    public JpaVendorAdapter mainJpaVendorAdapter(MainJpaProperties mainJpaProperties) {
        AbstractJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setShowSql(mainJpaProperties.isShowSql());
        jpaVendorAdapter.setDatabasePlatform(mainJpaProperties.getDatabasePlatform());
        jpaVendorAdapter.setGenerateDdl(mainJpaProperties.isGenerateDdl());
        return jpaVendorAdapter;
    }

}
