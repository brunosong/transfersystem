package com.brunosong.transfer.system.metaupload.service.dataaccess.metaitem.repository;

import com.brunosong.transfer.system.metaupload.service.dataaccess.config.MockJpaConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

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