package com.brunosong.transfer.system.transfer.service.ports.input.service;

import com.brunosong.transfer.system.transfer.service.dto.create.TransferRequest;
import com.brunosong.transfer.system.transfer.service.dto.create.CreateTransferResponse;

public interface TransferApplicationService {

    CreateTransferResponse executeTransfer(TransferRequest transferRequest);

}
