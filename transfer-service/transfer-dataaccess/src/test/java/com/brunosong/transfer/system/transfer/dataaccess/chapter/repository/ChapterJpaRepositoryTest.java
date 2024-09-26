package com.brunosong.transfer.system.transfer.dataaccess.chapter.repository;

import com.brunosong.transfer.system.transfer.dataaccess.config.MockConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {DataSourceAutoConfiguration.class, MockConfig.class, ChapterJpaRepository.class})
class ChapterJpaRepositoryTest {

    @Autowired
    ChapterJpaRepository chapterJpaRepository;

    @Test
    void MainChap가_정상적으로_저장되어_SEQ가_생성된다(){

//        ChapterEntity mainChap = ChapterEntity.builder()
//                .chapTitle("테스트 차시")
//                .chapType(ChapterEntity.ChapterTypeEnum.MATH)
//                .build();
//
//        ChapterEntity save = chapterJpaRepository.save(mainChap);
//        Assertions.assertThat(save.getChapSeq()).isNotNull();

    }
}