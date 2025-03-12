package com.brunosong.transfer.system.transfer.service.helper.transfer;

import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceContentData;

public abstract class TransferSendHelper {
    public void transferAction(Transfer transfer, SourceContentData sourceContentData) {
        try {
            doTransfer(transfer, sourceContentData);
            transfer.markSent();
        } catch (Exception e) {
            transfer.markFailed();
            throw e;
        }
    }

    protected abstract void doTransfer(Transfer transfer, SourceContentData sourceContentData);
}
