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


}
