package com.brunosong.transfer.system.main.controller;

import com.brunosong.transfer.system.main.service.TranService;
import com.brunosong.transfer.system.main.service.exception.TranCustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Locale;

//@RestController
//@RequiredArgsConstructor
public class TransController {

//    private final TranService tranService;
//    private final MessageSource messageSource;

//    @PostMapping("/doTran")
//    public ResponseEntity<TranActionRespDto> doTran(@Valid @RequestBody TranActionReqDto tranActionReqDto,
//                                                    BindingResult bindingResult) {
//
//        if (bindingResult.hasErrors()) {
//            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
//            return new ResponseEntity<>(new TranActionRespDto(errorMessage), HttpStatus.BAD_REQUEST);
//        }
//
//        if(tranActionReqDto.getTargetService().equals("aiService")) {
//            tranService.aiServiceTransferProcess(tranActionReqDto);
//        } else if(tranActionReqDto.getTargetService().equals("aiKafkaService")) {
//            tranService.aiKafkaServiceTransferProcess(tranActionReqDto);
//        }
//
//        String message = messageSource.getMessage("transfer.success",
//                new Object[]{tranActionReqDto.getTargetService(), tranActionReqDto.getDbProfile()}, Locale.getDefault());
//
//        return new ResponseEntity<>(new TranActionRespDto(message), HttpStatus.OK);
//
//    }
//
//    @ExceptionHandler(TranCustomException.class)
//    public ResponseEntity<TranActionRespDto> handleTranCustomException(TranCustomException ex) {
//        return new ResponseEntity<>(new TranActionRespDto(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
//    }

}
