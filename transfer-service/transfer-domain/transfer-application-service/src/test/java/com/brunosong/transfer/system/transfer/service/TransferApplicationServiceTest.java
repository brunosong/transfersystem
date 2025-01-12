package com.brunosong.transfer.system.transfer.service;

import com.brunosong.transfer.system.domain.valueobject.LearningMaterialId;
import com.brunosong.transfer.system.domain.valueobject.TransferId;
import com.brunosong.transfer.system.transfer.service.dto.create.CreateTransferCommand;
import com.brunosong.transfer.system.transfer.service.entity.LearningMaterial;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.LearningMaterialRepository;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.TransferLogRepository;
import com.brunosong.transfer.system.transfer.service.valueobject.TransType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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

    @Autowired
    TransferLogRepository transferLogRepository;

    private CreateTransferCommand command;

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

        UUID id = UUID.randomUUID();

        Transfer transfer = Transfer.builder()
                .id(new TransferId(id))
                .materialId(new LearningMaterialId("1111"))
                .build();
        when(transferLogRepository.save(any())).thenReturn(transfer);

       command = CreateTransferCommand.builder()
                .adminId(UUID.randomUUID())
                .transType(TransType.API)
                .materialId(id.toString())
                .build();

       transferExecutionHandler.execution(command);
    }
}
