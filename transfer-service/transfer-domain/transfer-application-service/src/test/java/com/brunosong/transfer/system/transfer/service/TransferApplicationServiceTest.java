package com.brunosong.transfer.system.transfer.service;

import com.brunosong.transfer.system.domain.valueobject.LearningMaterialId;
import com.brunosong.transfer.system.transfer.service.dto.excution.ExecutionTransferCommand;
import com.brunosong.transfer.system.transfer.service.entity.LearningMaterial;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.LearningMaterialRepository;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.TransferLogRepository;
import com.brunosong.transfer.system.transfer.service.valueobject.TransType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = TransferTestConfiguration.class)
public class TransferApplicationServiceTest {

    @Autowired
    LearningMaterialRepository learningMaterialRepository;

    private ExecutionTransferCommand command;

    @BeforeAll
    public void init() {

        LearningMaterial learningMaterial = LearningMaterial.builder()
                .id(new LearningMaterialId("ABCD"))
                .description("TEST")
                .title("코스1")
                .build();
        when(learningMaterialRepository.findById(any())).thenReturn(Optional.of(learningMaterial));
    }

    @Autowired
    TransferExecutionHandler transferExecutionHandler;

    @Test
    void test() {
       command = ExecutionTransferCommand.builder()
                .adminId("BrunoSong")
                .transType(TransType.API)
                .materialId(UUID.randomUUID().toString())
                .build();

       transferExecutionHandler.execution(command);
    }
}
