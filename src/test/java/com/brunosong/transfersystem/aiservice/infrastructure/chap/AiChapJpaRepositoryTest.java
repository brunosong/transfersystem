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
class AiChapJpaRepositoryTest {

    @Autowired
    AiChapJpaRepository aiChapJpaRepository;

    @Test
    void ChapEntity_정상적으로_저장된다(){
        AiChapEntity chapter1 = AiChapEntity.builder()
                .aiChapSeq(1L)
                .aiChapTitle("테스트 차시")
                .build();

        AiChapEntity save = aiChapJpaRepository.save(chapter1);
        Assertions.assertThat(save).isNotNull();
    }



}