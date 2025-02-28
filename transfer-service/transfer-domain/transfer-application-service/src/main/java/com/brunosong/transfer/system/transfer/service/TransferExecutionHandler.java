package com.brunosong.transfer.system.transfer.service;

import com.brunosong.transfer.system.transfer.service.dto.create.CreateTransferCommand;
import com.brunosong.transfer.system.transfer.service.entity.LearningMaterial;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.mapper.TransferDataMapper;
import com.brunosong.transfer.system.transfer.service.valueobject.TransType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransferExecutionHandler {

    private final LearningMaterialCreateHelper learningMaterialCreateHelper;
    private final TransferApiSendHelper transferApiSendHelper;
    private final TransferMessagingSendHelper messagingSendHelper;
    private final TransferDataMapper transferDataMapper;

    @Transactional
    public Transfer sendData(CreateTransferCommand createTransferCommand) {

        Transfer transfer =
                transferDataMapper.createTransferCommandToTransfer(createTransferCommand);

        transfer.initializeTransfer();

        LearningMaterial material =
                learningMaterialCreateHelper.findMaterialData(createTransferCommand.getMaterialId());

        try {
            if (createTransferCommand.getTransType() == TransType.API) {
                transferApiSendHelper.transferAction(transfer, material);
                log.info("API transfer completed: {}", transfer.getId());
            } else if (createTransferCommand.getTransType() == TransType.MESSAGING) {
                messagingSendHelper.transferAction(transfer, material);
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
