package com.brunosong.transfersystem.main.controller;

import com.brunosong.transfersystem.aiservice.infrastructure.chap.AiChapJpaRepository;
import com.brunosong.transfersystem.main.dto.TranActionDto.TranActionReqDto;
import com.brunosong.transfersystem.main.dto.TranActionDto.TranActionRespDto;
import com.brunosong.transfersystem.main.service.TranService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TransController {

    private final TranService tranService;

    @PostMapping("/doTran")
    public ResponseEntity<TranActionRespDto> doTran(@RequestBody TranActionReqDto tranActionReqDto,
                                                    BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return new ResponseEntity<>(new TranActionRespDto(errorMessage), HttpStatus.BAD_REQUEST);
        }

        if(tranActionReqDto.getTargetService().equals("aiService")) {
            tranService.aiServiceTransferProcess(tranActionReqDto);
        } else if(tranActionReqDto.getTargetService().equals("aiKafkaService")) {
            tranService.aiKafkaServiceTransferProcess(tranActionReqDto);
        }

        // 메시지 생성 Todo. MessageSource 로 바꿔야 한다.
        String message = "정상적으로 targetService: " + tranActionReqDto.getTargetService() +
                " and dbProfile: " + tranActionReqDto.getDbProfile() + "로 이관되었습니다.";

        return new ResponseEntity<>(new TranActionRespDto(message), HttpStatus.OK);

    }


}
