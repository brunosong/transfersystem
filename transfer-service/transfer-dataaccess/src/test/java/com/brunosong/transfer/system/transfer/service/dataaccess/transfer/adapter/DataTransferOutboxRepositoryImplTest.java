package com.brunosong.transfer.system.transfer.service.dataaccess.transfer.adapter;

import com.brunosong.transfer.system.transfer.service.dataaccess.TestJpaConfiguration;
import com.brunosong.transfer.system.transfer.service.dataaccess.transfer.mapper.TransferDataAccessMapper;
import com.brunosong.transfer.system.transfer.service.dataaccess.transfer.repository.TransferOutboxJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;


@ContextConfiguration(classes = { TestJpaConfiguration.class , DataTransferOutboxRepositoryImpl.class ,
                                    TransferOutboxJpaRepository.class, TransferDataAccessMapper.class })
@TestPropertySource(properties = {"spring.jpa.show-sql=true"})
@DataJpaTest
class DataTransferOutboxRepositoryImplTest {

    @Autowired
    private DataTransferOutboxRepositoryImpl dataTransferOutboxRepository;

    @Test
    void save() {
        // dataTransferOutboxRepository.save()


    }
}