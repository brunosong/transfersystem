package com.brunosong.transfer.system.transfer.service.dataaccess.transfer.adapter;

import com.brunosong.transfer.system.domain.valueobject.LearningMaterialId;
import com.brunosong.transfer.system.domain.valueobject.TransferLogId;
import com.brunosong.transfer.system.transfer.service.dataaccess.TestJpaConfiguration;
import com.brunosong.transfer.system.transfer.service.dataaccess.transfer.mapper.TransferLogDataAccessMapper;
import com.brunosong.transfer.system.transfer.service.entity.TransferLog;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;
import java.util.UUID;

@ContextConfiguration(classes = { TestJpaConfiguration.class,
                            TransferLogRepositoryImpl.class,
                            TransferLogDataAccessMapper.class
})
@TestPropertySource(properties = {"spring.jpa.show-sql=true"})
@DataJpaTest
class TransferLogRepositoryImplTest {

    @Autowired
    TransferLogRepositoryImpl transferLogRepository;

    @Test
    void save() {
        TransferLog transferLog = TransferLog.builder()
                .id(new TransferLogId(UUID.randomUUID()))
                .createAdminId("brunosong")
                .materialId(new LearningMaterialId("123456"))
                .build();

        TransferLog result = transferLogRepository.save(transferLog);

        Assertions.assertThat(result.getCreatedAt()).isNotNull();
    }

    @Test
    void findById() {

        TransferLog transferLog = TransferLog.builder()
                .id(new TransferLogId(UUID.randomUUID()))
                .createAdminId("brunosong")
                .materialId(new LearningMaterialId("123456"))
                .build();

        transferLogRepository.save(transferLog);
        Optional<TransferLog> byId = transferLogRepository.findById(transferLog.getId().getValue());

        Assertions.assertThat(transferLog.getId()).isEqualTo(byId.get().getId());
    }

}