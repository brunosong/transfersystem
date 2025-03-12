package com.brunosong.transfer.system.transfer.service;

import com.brunosong.transfer.system.transfer.service.dto.create.TransferRequest;
import com.brunosong.transfer.system.transfer.service.handler.TransferExecutionHandler;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.SourceRepository;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.TransferLogRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = TransferTestConfiguration.class)
public class TransferApplicationServiceTest {

    @Autowired
    SourceRepository sourceRepository;

    @Autowired
    TransferLogRepository transferLogRepository;

    private TransferRequest command;

    @BeforeAll
    public void init() {

//        LearningMaterial learningMaterial = LearningMaterial.builder()
//                .id(new LearningMaterialId("ABCD"))
//                .description("TEST")
//                .title("코스1")
//                .build();
//        when(sourceRepository.findById(any())).thenReturn(Optional.of(learningMaterial));
    }

    @Autowired
    TransferExecutionHandler transferExecutionHandler;

    @Test
    void test() {

        UUID id = UUID.randomUUID();

//        Transfer transfer = Transfer.builder()
//                .id(new TransferId(id))
//                .materialId(new LearningMaterialId("1111"))
//                .build();
//        when(transferLogRepository.save(any())).thenReturn(transfer);
//
//       command = TransferRequest.builder()
//                .adminId(UUID.randomUUID())
//                .transType(TransType.API)
//                .materialId(id.toString())
//                .build();
//
//       transferExecutionHandler.sendData(command);
    }
}
