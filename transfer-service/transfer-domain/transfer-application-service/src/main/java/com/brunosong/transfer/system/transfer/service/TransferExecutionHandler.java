package com.brunosong.transfer.system.transfer.service;

import com.brunosong.transfer.system.transfer.service.dto.create.CreateTransferResponse;
import com.brunosong.transfer.system.transfer.service.dto.create.CreateTransferCommand;
import com.brunosong.transfer.system.transfer.service.entity.LearningMaterial;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.mapper.TransferDataMapper;
import com.brunosong.transfer.system.transfer.service.valueobject.TransType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TransferExecutionHandler {

    private final LearningMaterialCreateHelper learningMaterialCreateHelper;
    private final TransferApiSendHelper transferApiSendHelper;
    private final TransferMessagingSendHelper messagingSendHelper;
    private final TransferCreateHelper transferCreateHelper;
    private final TransferDataMapper transferDataMapper;

    @Transactional
    public CreateTransferResponse execution(CreateTransferCommand createTransferCommand) {

        LearningMaterial material =
                learningMaterialCreateHelper.createMaterial(createTransferCommand);

        if (createTransferCommand.getTransType() == TransType.API) {
            transferApiSendHelper.transferAction(material);
        } else if (createTransferCommand.getTransType() == TransType.MESSAGING) {
            messagingSendHelper.transferAction(material);
        }

        Transfer transfer = transferCreateHelper.persistTransferLog(createTransferCommand);

        CreateTransferResponse createTransferResponse =
                transferDataMapper.transferLogToExcutionTransferResponse(transfer, "Create transfer log success");

        return createTransferResponse;
    }

}
