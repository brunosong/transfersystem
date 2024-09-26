package com.brunosong.transfer.system.transfer.service;

import com.brunosong.transfer.system.transfer.service.dto.excution.ExcutionTransferCommand;
import com.brunosong.transfer.system.transfer.service.dto.excution.ExcutionTransferResponse;
import com.brunosong.transfer.system.transfer.service.ports.input.service.TransferApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@Service
public class TransferApplicationServiceImpl implements TransferApplicationService {

    @Override
    public ExcutionTransferResponse excutionTransfer(ExcutionTransferCommand excutionTransferCommand) {
        return null;
    }
}
