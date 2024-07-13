package com.brunosong.transfersystem.main.controller;

import com.brunosong.transfersystem.aiservice.infrastructure.chap.AiChapJpaRepository;
import com.brunosong.transfersystem.config.annotation.UseAiServiceDevDataSource;
import com.brunosong.transfersystem.config.annotation.UseAiServiceRealDataSource;
import com.brunosong.transfersystem.main.dto.TranActionDto;
import com.brunosong.transfersystem.main.service.TranService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TransController {

    private final TranService tranService;

    private final AiChapJpaRepository aiChapJpaRepository;

    @GetMapping("/doTran")
    public ResponseEntity<?> doTran(@RequestParam("targetService") String targetService,
                                    @RequestParam("dbProfile") String dbProfile) {

        TranActionDto tranActionDto = new TranActionDto();
        tranActionDto.setTargetService(targetService);
        tranActionDto.setDbProfile(dbProfile);

        if(tranActionDto.getTargetService().equals("aiService")) {
            tranService.aiServiceTransferProcess(tranActionDto);
        } else if(tranActionDto.getTargetService().equals("aiKafkaService")) {
            tranService.aiKafkaServiceTransferProcess(tranActionDto);
        }

        return new ResponseEntity<>("Hello",HttpStatus.OK);

    }


    @UseAiServiceRealDataSource
    @GetMapping("/checkAiReal")
    public ResponseEntity<?> checkAiReal() {

        System.out.println(aiChapJpaRepository.findAll());

        return new ResponseEntity<>("Hello",HttpStatus.OK);

    }


    @UseAiServiceDevDataSource
    @GetMapping("/checkAiDev")
    public ResponseEntity<?> checkADev() {

        System.out.println(aiChapJpaRepository.findAll());

        return new ResponseEntity<>("Hello",HttpStatus.OK);

    }

}
