package com.brunosong.transfer.system.transfer.service.helper.transfer;

import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransferMessagingSendHelper extends TransferSendHelper {

    private final List<SourceTypePublisher> sourceTypePublishers;

    @Override
    protected void doTransfer(Transfer transfer) {

        SourceType sourceType = transfer.getSourceContentData().getSourceType();
        SourceTypePublisher selector = sourceTypePublishers.stream()
                .filter(ts -> isMatchingSelector(ts, sourceType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No matching TypeSelector for " + sourceType));

        selector.typePublisher(transfer);
    }

    private boolean isMatchingSelector(SourceTypePublisher selector, SourceType sourceType) {
        if (selector instanceof CurriculumSourceTypePublisher && sourceType == SourceType.CURRICULUM) {
            return true;
        } else if (selector instanceof ExamResultDataSourceTypePublisher && sourceType == SourceType.EXAM_RESULT_DATA) {
            return true;
        }
        // 다른 타입 추가 시 여기에 조건 추가
        return false;
    }


}
