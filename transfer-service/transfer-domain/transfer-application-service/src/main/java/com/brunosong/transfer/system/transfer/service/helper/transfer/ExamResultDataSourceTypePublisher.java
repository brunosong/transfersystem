package com.brunosong.transfer.system.transfer.service.helper.transfer;

import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.ports.output.message.publisher.TransferDataSendMessagePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExamResultDataSourceTypePublisher implements SourceTypePublisher {

    private final TransferDataSendMessagePublisher transferDataSendMessagePublisher;

    @Override
    public void typePublisher(Transfer transfer) {
        transferDataSendMessagePublisher.publish(transfer);
    }
}
