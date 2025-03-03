package com.brunosong.transfer.system.transfer.service;

import com.brunosong.transfer.system.domain.valueobject.LearningMaterialId;
import com.brunosong.transfer.system.domain.valueobject.TransferId;
import com.brunosong.transfer.system.transfer.service.dto.create.CreateTransferCommand;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.exception.MaterialNotFoundException;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.LearningMaterialRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = TransferTestConfiguration.class)
class LearningMaterialCreateHelperTest {

    @Autowired
    private LearningMaterialCreateHelper learningMaterialCreateHelper;

    @Autowired
    private LearningMaterialRepository learningMaterialRepository;

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
    @DisplayName("Material이 없으면 MaterialNotFoundException 발생")
    void createMaterial() {
//        when(learningMaterialRepository.findById(any())).thenReturn(Optional.empty());
//        Assertions.assertThatThrownBy(()-> learningMaterialCreateHelper.findMaterialData(command))
//                .isInstanceOf(MaterialNotFoundException.class);
    }

}