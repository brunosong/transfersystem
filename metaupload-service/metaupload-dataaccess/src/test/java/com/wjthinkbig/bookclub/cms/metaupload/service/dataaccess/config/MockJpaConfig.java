package com.wjthinkbig.bookclub.cms.metaupload.service.dataaccess.config;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;

//@EnableJpaRepositories(basePackages = "com.wjthinkbig.bookclub.cms.metaupload.service.dataaccess")
//@EntityScan(basePackages = { "com.wjthinkbig.bookclub.cms.metaupload.service.dataaccess"})
@SpringBootConfiguration
public class MockJpaConfig {

//    @Bean
//    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
//        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
//        em.setDataSource(dataSource);
//        em.setPackagesToScan("com.wjthinkbig.bookclub.cms.metaupload.service.dataaccess");  // 엔티티가 위치한 패키지
//
//        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//        em.setJpaVendorAdapter(vendorAdapter);
//
//        return em;
//    }

}
