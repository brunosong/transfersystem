package com.brunosong.transfersystem.aiservice.infrastructure.chap;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("jpa-test")
class ChapJpaRepositoryTest {

    @Autowired
    ChapJpaRepository chapJpaRepository;

    @Test
    void ChapEntity가_정상적으로_저장되어_SEQ가_생성된다(){
        ChapEntity chapter1 = ChapEntity.builder()
                .chapTitle("테스트 차시")
                .build();

        ChapEntity save = chapJpaRepository.save(chapter1);

        Assertions.assertThat(save.getChapSeq()).isNotNull();
    }



}