package com.brunosong.transfersystem.main.domain.chap;

import com.brunosong.transfersystem.main.domain.chap.MainChap.MainChapTypeEnum;
import com.brunosong.transfersystem.main.repository.MainChapRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("jpa-test")
class MainAiChapTestChapEntity {

    @Autowired
    MainChapRepository mainChapRepository;

    @Test
    void MainChap가_정상적으로_저장되어_SEQ가_생성된다(){

        MainChap mainChap = MainChap.builder()
                .chapTitle("테스트 차시")
                .chapType(MainChapTypeEnum.MATH)
                .build();

        MainChap save = mainChapRepository.save(mainChap);
        Assertions.assertThat(save.getChapSeq()).isNotNull();

    }

}