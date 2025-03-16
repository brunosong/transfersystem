package com.brunosong.transfer.system.transfer.service.handler;

import com.brunosong.transfer.system.transfer.service.helper.transfer.TransferApiSendHelper;
import com.brunosong.transfer.system.transfer.service.dto.create.TransferRequest;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.helper.source.SourceFindHelper;
import com.brunosong.transfer.system.transfer.service.mapper.TransferDataMapper;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceContentData;
import com.brunosong.transfer.system.transfer.service.valueobject.TransType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransferExecutionHandler {

    private final TransferApiSendHelper transferApiSendHelper;
    private final TransferMessagingSendHelper messagingSendHelper;
    private final TransferDataMapper transferDataMapper;

    @Transactional
    public Transfer sendData(Transfer transfer) {

        try {
            if (transfer.getTransType() == TransType.API) {
                transferApiSendHelper.transferAction(transfer);
                log.info("API transfer completed: {}", transfer.getId());
            } else if (transfer.getTransType() == TransType.MESSAGING) {
                messagingSendHelper.transferAction(transfer);
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
