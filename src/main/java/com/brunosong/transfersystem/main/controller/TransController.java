package com.brunosong.transfersystem.main.controller;

import com.brunosong.transfersystem.main.service.MigrationServiceSelector;
import com.brunosong.transfersystem.main.service.ServiceSelector2;
import com.brunosong.transfersystem.main.service.MigrationService;
import com.brunosong.transfersystem.main.service.TranService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TransController {

    private final TranService tranService;
    private final MigrationServiceSelector migrationServiceSelector;

    private final ServiceSelector2 serviceSelector2;

    private final ApplicationContext ac;

    @GetMapping("/doTran")
    public ResponseEntity<?> doTran() throws InterruptedException {

        tranService.aiServiceDbTransProcess(1L);

        return new ResponseEntity<>("Hello",HttpStatus.OK);
    }

    @GetMapping("/doTran2")
    public ResponseEntity<?> doTran2() throws InterruptedException {

        migrationServiceSelector.useAiService();
        tranService.aiServiceKafkaTransProcess(1L);

        return new ResponseEntity<>("Hello",HttpStatus.OK);
    }

    @GetMapping("/doTran3")
    public ResponseEntity<?> doTran3() throws InterruptedException {

        tranService.aiServiceDbTransProcess(1L);

        return new ResponseEntity<>("Hello",HttpStatus.OK);
    }

    @GetMapping("/doTran4")
    public ResponseEntity<?> doTran4() throws InterruptedException {
        MigrationService tranActionAiKafkaService = ac.getBean("tranActionAiKafkaService", MigrationService.class);
        serviceSelector2.useServiceA(tranActionAiKafkaService);
        tranService.aiServiceKafkaTransProcess(1L);

        return new ResponseEntity<>("Hello",HttpStatus.OK);
    }
}
