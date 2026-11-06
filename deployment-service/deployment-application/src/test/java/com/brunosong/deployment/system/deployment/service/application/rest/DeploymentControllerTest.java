package com.brunosong.deployment.system.deployment.service.application.rest;

import com.brunosong.deployment.system.domain.valueobject.DeploymentStatus;
import com.brunosong.deployment.system.deployment.service.application.exception.handler.DeploymentGlobalExceptionHandler;
import com.brunosong.deployment.system.deployment.service.dto.create.CreateDeploymentResponse;
import com.brunosong.deployment.system.deployment.service.dto.query.PageView;
import com.brunosong.deployment.system.deployment.service.dto.query.DeploymentSearchCondition;
import com.brunosong.deployment.system.deployment.service.exception.DeploymentNotFoundException;
import com.brunosong.deployment.system.deployment.service.ports.input.service.DeploymentApplicationService;
import com.brunosong.deployment.system.deployment.service.ports.input.service.DeploymentQueryService;
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

class DeploymentControllerTest {

    private final DeploymentApplicationService applicationService = mock(DeploymentApplicationService.class);
    private final DeploymentQueryService queryService = mock(DeploymentQueryService.class);
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new DeploymentController(applicationService, queryService))
                .setControllerAdvice(new DeploymentGlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("배포 접수는 202 와 조회 위치를 돌려준다")
    void createReturnsAccepted() throws Exception {
        UUID id = UUID.randomUUID();
        when(applicationService.executeDeployment(any())).thenReturn(new CreateDeploymentResponse(id, 4));

        mockMvc.perform(post("/api/v1/deployments").contentType(MediaType.APPLICATION_JSON).content("""
                        {"targetId": 1, "transType": "MESSAGING",
                         "sourceId": "curriculum-001", "sourceConfigId": 4}
                        """))
                .andExpect(status().isAccepted())
                .andExpect(header().string("Location", "/api/v1/deployments/" + id))
                .andExpect(jsonPath("$.deploymentId").value(id.toString()))
                .andExpect(jsonPath("$.chunkCount").value(4));
    }

    @Test
    @DisplayName("필수 값이 빠지면 400 과 빠진 필드를 알려 준다")
    void createValidates() throws Exception {
        mockMvc.perform(post("/api/v1/deployments").contentType(MediaType.APPLICATION_JSON).content("""
                        {"transType": "MESSAGING", "sourceConfigId": 4}
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("sourceId")));
    }

    @Test
    @DisplayName("목록은 상태와 페이지 조건을 서비스로 넘긴다")
    void listPassesCondition() throws Exception {
        when(queryService.findDeployments(any())).thenReturn(new PageView<>(List.of(), 1, 10, 0, 0));

        mockMvc.perform(get("/api/v1/deployments").param("status", "FAILED").param("page", "1").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(1));

        verify(queryService).findDeployments(new DeploymentSearchCondition(DeploymentStatus.FAILED, 1, 10));
    }

    @Test
    @DisplayName("모르는 상태 값은 400")
    void listRejectsUnknownStatus() throws Exception {
        mockMvc.perform(get("/api/v1/deployments").param("status", "NOPE"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("없는 배포는 404")
    void detailNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(queryService.findDeployment(id)).thenThrow(new DeploymentNotFoundException("Deployment not found: " + id));

        mockMvc.perform(get("/api/v1/deployments/{id}", id))
                .andExpect(status().isNotFound());
    }
}
