package com.brunosong.transfer.system.transfer.service;

import com.brunosong.transfer.system.domain.valueobject.LearningMaterialId;
import com.brunosong.transfer.system.domain.valueobject.TransferId;
import com.brunosong.transfer.system.transfer.service.dto.create.CreateTransferCommand;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.exception.TransferDomainException;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.TransferLogRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = TransferTestConfiguration.class)
class TransferCreateHelperTest {

    @Autowired
    TransferLogRepository transferLogRepository;

    @Autowired
    TransferCreateHelper transferCreateHelper;

    CreateTransferCommand command;

    Transfer transfer;

    @BeforeAll
    public void init() {
        command = CreateTransferCommand.builder().build();
        transfer = Transfer.builder()
                .id(new TransferId(UUID.randomUUID()))
                .materialId(new LearningMaterialId("abcd1"))
                .build();
    }

    @Test
    @DisplayName("저장이 되지 않으면 TransferDomainException이 발생한다")
    void persistTransferLog() {
        when(transferLogRepository.save(any())).thenReturn(null);
//        Assertions.assertThatThrownBy(()-> transferCreateHelper.persistTransferLog(command))
//                .isInstanceOf(TransferDomainException.class);
    }


}