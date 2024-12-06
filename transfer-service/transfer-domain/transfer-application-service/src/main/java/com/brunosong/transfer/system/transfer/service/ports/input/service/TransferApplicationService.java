package com.brunosong.transfer.system.transfer.service.ports.input.service;

import com.brunosong.transfer.system.transfer.service.dto.create.CreateTransferCommand;
import com.brunosong.transfer.system.transfer.service.dto.create.CreateTransferResponse;

public interface TransferApplicationService {

    CreateTransferResponse createTransfer(CreateTransferCommand createTransferCommand);

}
