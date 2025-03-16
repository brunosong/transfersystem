package com.brunosong.transfer.system.transfer.service;

import com.brunosong.transfer.system.transfer.service.dto.create.TransferRequest;
import com.brunosong.transfer.system.transfer.service.dto.create.CreateTransferResponse;
import com.brunosong.transfer.system.transfer.service.dto.create.TransferLogResult;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.handler.SourceFindDataHandler;
import com.brunosong.transfer.system.transfer.service.handler.TransferExecutionHandler;
import com.brunosong.transfer.system.transfer.service.handler.TransferLogHandler;
import com.brunosong.transfer.system.transfer.service.mapper.TransferDataMapper;
import com.brunosong.transfer.system.transfer.service.ports.input.service.TransferApplicationService;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceContentData;
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
    private final TransferLogHandler transferLogHandler;
    private final TransferDataMapper transferDataMapper;
    private final TransferDomainService transferDomainService;
    private final SourceFindDataHandler sourceFindDataHandler;

    @Override
    public CreateTransferResponse executeTransfer(TransferRequest transferRequest) {

        Transfer transfer =
                transferDataMapper.transferRequestToTransfer(transferRequest);

        // 1. 전송할 데이터 찾기
        SourceContentData sourceContentData = sourceFindDataHandler.findSourceContentData(transfer);

        transferDomainService.validateAndInitiateTransfer(transfer, sourceContentData);

        // 2. 데이터 전송
        transfer = transferExecutionHandler.sendData(transfer);

        // 2. 데이터 전송 로그 저장
        TransferLogResult transferLogResult = transferLogHandler.persistTransferLog(transfer);

        return transferDataMapper.transferLogToExecutionTransferResponse(transferLogResult);
    }
}
