package com.brunosong.transfersystem.main.controller;

import com.brunosong.transfersystem.main.dto.TranActionDto;
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

    @GetMapping("/doTran")
    public ResponseEntity<?> doTran() {

        TranActionDto tranActionDto = new TranActionDto();
        tranActionDto.setTargetService("aiService");
        tranActionDto.setDbProfile("real");

        if(tranActionDto.getTargetService().equals("aiService")) {
            tranService.aiServiceTransferProcess(tranActionDto);
        } else if(tranActionDto.getTargetService().equals("aiKafkaService")) {
            tranService.aiKafkaServiceTransferProcess(tranActionDto);
        }
        return new ResponseEntity<>("Hello",HttpStatus.OK);

    }

}
