package com.brunosong.transfer.system.transfer.service.application.rest;

import com.brunosong.transfer.system.domain.valueobject.TransferStatus;
import com.brunosong.transfer.system.transfer.service.application.exception.handler.TransferGlobalExceptionHandler;
import com.brunosong.transfer.system.transfer.service.dto.create.CreateTransferResponse;
import com.brunosong.transfer.system.transfer.service.dto.query.PageView;
import com.brunosong.transfer.system.transfer.service.dto.query.TransferSearchCondition;
import com.brunosong.transfer.system.transfer.service.exception.TransferLogNotFoundException;
import com.brunosong.transfer.system.transfer.service.ports.input.service.TransferApplicationService;
import com.brunosong.transfer.system.transfer.service.ports.input.service.TransferQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TransferControllerTest {

    private final TransferApplicationService applicationService = mock(TransferApplicationService.class);
    private final TransferQueryService queryService = mock(TransferQueryService.class);
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TransferController(applicationService, queryService))
                .setControllerAdvice(new TransferGlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("전송 접수는 202 와 조회 위치를 돌려준다")
    void createReturnsAccepted() throws Exception {
        UUID id = UUID.randomUUID();
        when(applicationService.executeTransfer(any())).thenReturn(new CreateTransferResponse(id, 4));

        mockMvc.perform(post("/api/v1/transfers").contentType(MediaType.APPLICATION_JSON).content("""
                        {"dataMigrationInfoId": 1, "transType": "MESSAGING",
                         "sourceId": "curriculum-001", "sourceConfigId": 4}
                        """))
                .andExpect(status().isAccepted())
                .andExpect(header().string("Location", "/api/v1/transfers/" + id))
                .andExpect(jsonPath("$.transferId").value(id.toString()))
                .andExpect(jsonPath("$.chunkCount").value(4));
    }

    @Test
    @DisplayName("필수 값이 빠지면 400 과 빠진 필드를 알려 준다")
    void createValidates() throws Exception {
        mockMvc.perform(post("/api/v1/transfers").contentType(MediaType.APPLICATION_JSON).content("""
                        {"transType": "MESSAGING", "sourceConfigId": 4}
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("sourceId")));
    }

    @Test
    @DisplayName("목록은 상태와 페이지 조건을 서비스로 넘긴다")
    void listPassesCondition() throws Exception {
        when(queryService.findTransfers(any())).thenReturn(new PageView<>(List.of(), 1, 10, 0, 0));

        mockMvc.perform(get("/api/v1/transfers").param("status", "FAILED").param("page", "1").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(1));

        verify(queryService).findTransfers(new TransferSearchCondition(TransferStatus.FAILED, 1, 10));
    }

    @Test
    @DisplayName("모르는 상태 값은 400")
    void listRejectsUnknownStatus() throws Exception {
        mockMvc.perform(get("/api/v1/transfers").param("status", "NOPE"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("없는 전송은 404")
    void detailNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(queryService.findTransfer(id)).thenThrow(new TransferLogNotFoundException("Transfer not found: " + id));

        mockMvc.perform(get("/api/v1/transfers/{id}", id))
                .andExpect(status().isNotFound());
    }
}
