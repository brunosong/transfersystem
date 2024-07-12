package com.brunosong.transfersystem.main.repository;

import com.brunosong.transfersystem.main.domain.chap.MainChap;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("jpa-test")
@Sql("/data.sql")
class MainChapRepositoryTest {


    @Autowired
    MainChapRepository mainChapRepository;


    @Test
    void test() {

        List<MainChap> all = mainChapRepository.findAll();
    }





}