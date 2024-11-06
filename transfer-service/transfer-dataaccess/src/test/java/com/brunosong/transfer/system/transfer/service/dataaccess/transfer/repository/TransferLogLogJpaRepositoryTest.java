package com.brunosong.transfer.system.transfer.service.dataaccess.transfer.repository;

import com.brunosong.transfer.system.transfer.service.dataaccess.TestJpaConfiguration;
import com.brunosong.transfer.system.transfer.service.dataaccess.transfer.entity.TransferLogEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Optional;
import java.util.UUID;

@TestPropertySource(properties = {"spring.jpa.show-sql=true"})
@ContextConfiguration(classes = {TestJpaConfiguration.class})
@DataJpaTest
class TransferLogLogJpaRepositoryTest {

    @Autowired
    TransferLogJpaRepository jpaRepository;

    @Autowired
    EntityManager entityManager;

    @Transactional
    @Test
    void test() {
        UUID uuid = UUID.randomUUID();
        TransferLogEntity brunoSong = TransferLogEntity.builder()
                .id(uuid)
                .createAdminId("BrunoSong")
                .build();
    }

}