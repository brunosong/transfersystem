package com.brunosong.transfersystem.main.controller;

import com.brunosong.transfersystem.main.service.TranService;
import com.brunosong.transfersystem.main.service.exception.TranCustomException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransController.class)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class TransControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TranService tranService;

    @MockBean
    private MessageSource messageSource;

    @Test
    public void 정상실행_메시지_출력() throws Exception {
        String body = "{ \"targetService\": \"aiService\", \"dbProfile\": \"testProfile\" }";

        given(messageSource.getMessage("transfer.success", new Object[]{"aiService", "testProfile"}, Locale.getDefault()))
                        .willReturn("정상적으로 targetService: aiService and dbProfile: testProfile로 이관되었습니다.");

        mockMvc.perform(    post("/doTran")
                            .contentType(MediaType.valueOf("application/json"))
                            .content(body)
                ).andExpect(status().isOk())
                 .andExpect( jsonPath("$.message").value("정상적으로 targetService: aiService and dbProfile: testProfile로 이관되었습니다.") );
    }


    @Test
    public void 벨리데이션_작동으로_에러가_발생하여_400과_에러메시지가_발생한다() throws Exception {

        String body = "{\"dbProfile\": \"testProfile\"}";

        mockMvc.perform(post("/doTran")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                ).andExpect(status().isBadRequest())
                 .andExpect(jsonPath("$.message").value("Target service is required"));
    }



    @Test
    public void 서비스에서_에러가_발생하여_익셉션핸들러가_정상작동한다() throws Exception {

        String body = "{ \"targetService\": \"aiService\", \"dbProfile\": \"testProfile\" }";

        doThrow(new TranCustomException("test exception")).when(tranService).aiServiceTransferProcess(any());

        mockMvc.perform(post("/doTran")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                ).andExpect(status().is5xxServerError())
                 .andExpect(jsonPath("$.message").value("test exception"));
    }


}