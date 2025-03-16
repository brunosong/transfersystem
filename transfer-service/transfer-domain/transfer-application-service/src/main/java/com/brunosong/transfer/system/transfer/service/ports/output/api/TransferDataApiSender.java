package com.brunosong.transfer.system.transfer.service.ports.output.api;

import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceContentData;

public interface TransferDataApiSender {
    void sendLearningMaterial(Transfer transfer);
}
