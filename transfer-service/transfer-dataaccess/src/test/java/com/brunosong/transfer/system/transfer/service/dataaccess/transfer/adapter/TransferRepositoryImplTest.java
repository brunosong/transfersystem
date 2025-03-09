package com.brunosong.transfer.system.transfer.service.dataaccess.transfer.adapter;

import com.brunosong.transfer.system.domain.valueobject.DataMigrationInfoId;
import com.brunosong.transfer.system.domain.valueobject.TransferId;
import com.brunosong.transfer.system.domain.valueobject.TransferStatus;
import com.brunosong.transfer.system.transfer.service.dataaccess.TestJpaConfiguration;
import com.brunosong.transfer.system.transfer.service.dataaccess.transfer.mapper.TransferLogDataAccessMapper;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.UUID;

@ContextConfiguration(classes = { TestJpaConfiguration.class,
                            TransferLogRepositoryImpl.class,
                            TransferLogDataAccessMapper.class
})
@TestPropertySource(properties = {"spring.jpa.show-sql=true"})
@DataJpaTest
class TransferRepositoryImplTest {

    @Autowired
    TransferLogRepositoryImpl transferLogRepository;

    @Test
    void save() {
        Transfer transfer = Transfer.builder()
                .id(new TransferId(UUID.randomUUID()))
                .transferStatus(TransferStatus.PENDING)
                //.learningMaterialId(new LearningMaterialId(UUID.randomUUID().toString()))
                .dataMigrationInfoId(new DataMigrationInfoId(UUID.randomUUID()))
                //.createAdminId("brunosong")
               // .materialId(new LearningMaterialId("123456"))
                .build();

        Transfer result = transferLogRepository.save(transfer);

        Assertions.assertThat(result.getCreatedAt()).isNotNull();
    }

    @Test
    void findById() {

//        Transfer transfer = Transfer.builder()
//                .id(new TransferId(UUID.randomUUID()))
//                .createAdminId("brunosong")
//                .materialId(new LearningMaterialId("123456"))
//                .build();
//
//        transferLogRepository.save(transfer);
//        Optional<Transfer> byId = transferLogRepository.findById(transfer.getId().getValue());
//
//        Assertions.assertThat(transfer.getId()).isEqualTo(byId.get().getId());
    }

}