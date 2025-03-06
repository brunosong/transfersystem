package com.brunosong.transfer.system.transfer.service;

import com.brunosong.transfer.system.transfer.service.dto.create.TransferRequest;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.mapper.TransferDataMapper;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceContentData;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceId;
import com.brunosong.transfer.system.transfer.service.valueobject.TransType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransferExecutionHandler {

    private final SourceFindHelper sourceFindHelper;
    private final TransferApiSendHelper transferApiSendHelper;
    private final TransferMessagingSendHelper messagingSendHelper;
    private final TransferDataMapper transferDataMapper;

    @Transactional
    public Transfer sendData(TransferRequest transferRequest) {

        Transfer transfer =
                transferDataMapper.transferRequestToTransfer(transferRequest);

        transfer.initializeTransfer();

        SourceContentData sourceContentData =
                sourceFindHelper.findData(transfer.getSourceType(), transfer.getSourceId());

        try {
            if (transferRequest.getTransType() == TransType.API) {
                transferApiSendHelper.transferAction(transfer, sourceContentData);
                log.info("API transfer completed: {}", transfer.getId());
            } else if (transferRequest.getTransType() == TransType.MESSAGING) {
                messagingSendHelper.transferAction(transfer, sourceContentData);
                log.info("Messaging transfer completed: {}", transfer.getId());
            } else {
                transfer.markFailed();
                log.warn("Unknown transfer type for: {}", transfer.getId());
            }
        } catch (Exception e) {
            transfer.markFailed();
            log.error("Transfer failed: {}", transfer.getId(), e);
        }

        return transfer;
    }

}
