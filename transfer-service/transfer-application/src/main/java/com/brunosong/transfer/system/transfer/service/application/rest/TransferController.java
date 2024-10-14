package com.brunosong.transfer.system.transfer.service.application.rest;

import com.brunosong.transfer.system.transfer.service.dto.excution.ExcutionTransferCommand;
import com.brunosong.transfer.system.transfer.service.dto.excution.ExcutionTransferResponse;
import com.brunosong.transfer.system.transfer.service.ports.input.service.TransferApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/transfer")
@RequiredArgsConstructor
public class TransferController {

    private final TransferApplicationService transferApplicationService;

    @PostMapping("/doTran")
    public ResponseEntity<ExcutionTransferResponse> doTran(@Valid @RequestBody ExcutionTransferCommand excutionTransferCommand,
                                                           BindingResult bindingResult) {

        ExcutionTransferResponse excutionTransferResponse = transferApplicationService.excutionServiceTransfer(excutionTransferCommand);
        return new ResponseEntity<>(excutionTransferResponse, HttpStatus.OK);
    }
}
