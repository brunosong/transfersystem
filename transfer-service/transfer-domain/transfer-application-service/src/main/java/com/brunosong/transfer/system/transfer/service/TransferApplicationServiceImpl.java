package com.brunosong.transfer.system.transfer.service;

import com.brunosong.transfer.system.transfer.service.dto.excution.ExecutionTransferCommand;
import com.brunosong.transfer.system.transfer.service.dto.excution.ExcutionTransferResponse;
import com.brunosong.transfer.system.transfer.service.ports.input.service.TransferApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@Service
@RequiredArgsConstructor
public class TransferApplicationServiceImpl implements TransferApplicationService {

    private final TransferExecutionHandler transferExecutionHandler;

    @Override
    public ExcutionTransferResponse executionTransfer(ExecutionTransferCommand executionTransferCommand) {
        return transferExecutionHandler.execution(executionTransferCommand);
    }
}
