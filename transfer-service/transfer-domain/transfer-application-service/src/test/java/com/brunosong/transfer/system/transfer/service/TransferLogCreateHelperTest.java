package com.brunosong.transfer.system.transfer.service;

import com.brunosong.transfer.system.domain.valueobject.LearningMaterialId;
import com.brunosong.transfer.system.domain.valueobject.TransferLogId;
import com.brunosong.transfer.system.transfer.service.dto.excution.ExecutionTransferCommand;
import com.brunosong.transfer.system.transfer.service.entity.TransferLog;
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
class TransferLogCreateHelperTest {

    @Autowired
    TransferLogRepository transferLogRepository;

    @Autowired
    TransferLogCreateHelper transferLogCreateHelper;

    ExecutionTransferCommand command;

    TransferLog transferLog;

    @BeforeAll
    public void init() {
        command = ExecutionTransferCommand.builder().build();
        transferLog = TransferLog.builder()
                .id(new TransferLogId(UUID.randomUUID()))
                .materialId(new LearningMaterialId("abcd1"))
                .build();
    }

    @Test
    @DisplayName("저장이 되지 않으면 TransferDomainException이 발생한다")
    void persistTransferLog() {
        when(transferLogRepository.save(any())).thenReturn(null);
        Assertions.assertThatThrownBy(()-> transferLogCreateHelper.persistTransferLog(command))
                .isInstanceOf(TransferDomainException.class);
    }


}