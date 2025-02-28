package com.brunosong.transfer.system.transfer.service;

import com.brunosong.transfer.system.transfer.service.entity.LearningMaterial;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;

public abstract class TransferSendHelper {
    public void transferAction(Transfer transfer, LearningMaterial material) {
        try {
            doTransfer(transfer, material);
            transfer.markSent();
        } catch (Exception e) {
            transfer.markFailed();
            throw e;
        }
    }

    protected abstract void doTransfer(Transfer transfer, LearningMaterial material);
}
