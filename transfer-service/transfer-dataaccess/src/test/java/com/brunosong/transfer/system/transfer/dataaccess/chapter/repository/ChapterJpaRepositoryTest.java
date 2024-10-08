package com.brunosong.transfer.system.transfer.dataaccess.chapter.repository;

import com.brunosong.transfer.system.transfer.dataaccess.chapter.entity.ChapterEntity;
import com.brunosong.transfer.system.transfer.dataaccess.DataAccessTestConfiguration;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;


@ContextConfiguration(classes = DataAccessTestConfiguration.class)
@DataJpaTest
class ChapterJpaRepositoryTest {

}