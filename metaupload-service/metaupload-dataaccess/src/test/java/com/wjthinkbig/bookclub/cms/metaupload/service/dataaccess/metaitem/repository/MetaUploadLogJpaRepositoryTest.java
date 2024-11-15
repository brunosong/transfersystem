package com.wjthinkbig.bookclub.cms.metaupload.service.dataaccess.metaitem.repository;

import com.wjthinkbig.bookclub.cms.metaupload.service.dataaccess.config.MockJpaConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.sql.DataSource;
import java.sql.SQLException;

//@SpringJUnitConfig(value = {DataSourceAutoConfiguration.class})
//@TestPropertySource(locations = "classpath:application.properties")
@SpringBootTest(classes = MockJpaConfig.class)
@DataJpaTest
class MetaUploadLogJpaRepositoryTest {

//    @Autowired
//    DataSource dataSource;

    @Test
    void 데이터소스_생성확인() throws SQLException {
//        System.out.println(dataSource.getConnection().getMetaData());
//        Assertions.assertThat(dataSource).isNotNull();
    }

}