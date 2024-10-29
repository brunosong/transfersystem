package com.brunosong.transfer.system.transfer.service.ports.input.service;

import com.brunosong.transfer.system.transfer.service.dto.excution.ExecutionTransferCommand;
import com.brunosong.transfer.system.transfer.service.dto.excution.ExcutionTransferResponse;

public interface TransferApplicationService {

    ExcutionTransferResponse executionTransfer(ExecutionTransferCommand executionTransferCommand);

}
