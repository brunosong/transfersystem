package com.brunosong.transfer.system.transfer.service.helper.transfer.exam;

import com.brunosong.transfer.system.transfer.service.config.annotation.SourceTypeSelector;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.helper.transfer.SourceTypePublisher;
import com.brunosong.transfer.system.transfer.service.ports.output.message.publisher.TransferDataSendMessagePublisher;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@SourceTypeSelector(
        sourceType = { SourceType.EXAM_RESULT_DATA }
)
public class ExamResultDataSourceTypePublisher implements SourceTypePublisher {

    private final TransferDataSendMessagePublisher transferDataSendMessagePublisher;

    @Override
    public void typePublisher(Transfer transfer) {
        transferDataSendMessagePublisher.publish(transfer);
    }
}
