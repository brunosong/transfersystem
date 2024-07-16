package com.brunosong.transfersystem.common;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ActiveProfiles;

import java.util.Locale;

/* MessageSource 만 테스트 하기 위해 MessageSourceAutoConfiguration 만 로드 */
@SpringBootTest(classes = MessageSourceAutoConfiguration.class)
@ActiveProfiles("test")
public class MessageSourceTest {

    @Autowired
    MessageSource messageSource;

    @Test
    void 메시지를_정상적으로_가져온다() {
        String message = messageSource.getMessage("transfer.success", new Object[]{"aiService", "testProfile"}, Locale.getDefault());
        Assertions.assertThat(message).isEqualTo("정상적으로 targetService: aiService and dbProfile: testProfile로 이관되었습니다.");
    }


}
