package com.brunosong.transfer.system.transfer.service.application.rest;

import com.brunosong.transfer.system.transfer.service.dto.create.TransferRequest;
import com.brunosong.transfer.system.transfer.service.dto.create.CreateTransferResponse;
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
    public ResponseEntity<CreateTransferResponse> doTran(@Valid @RequestBody TransferRequest transferRequest,
                                                         BindingResult bindingResult) {

        CreateTransferResponse createTransferResponse = transferApplicationService.executeTransfer(transferRequest);
        return new ResponseEntity<>(createTransferResponse, HttpStatus.OK);
    }
}
