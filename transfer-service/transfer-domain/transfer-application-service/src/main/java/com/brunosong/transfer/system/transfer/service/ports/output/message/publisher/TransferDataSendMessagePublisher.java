package com.brunosong.transfer.system.transfer.service.ports.output.message.publisher;

import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceContentData;

public interface TransferDataSendMessagePublisher {
    void publish(Transfer transfer);
}
