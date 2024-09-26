package com.brunosong.transfer.system.transfer.service.ports.input.service;

import com.brunosong.transfer.system.transfer.service.dto.excution.ExcutionTransferCommand;
import com.brunosong.transfer.system.transfer.service.dto.excution.ExcutionTransferResponse;

public interface TransferApplicationService {
    ExcutionTransferResponse excutionTransfer(ExcutionTransferCommand excutionTransferCommand);
}
