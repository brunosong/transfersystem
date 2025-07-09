package com.brunosong.transfer.system.transfer.service.helper.transfer;

import com.brunosong.transfer.system.transfer.service.config.annotation.SourceTypeSelector;
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

    private boolean isMatchingSelector(SourceTypePublisher typePublisher, SourceType sourceType) {

        SourceTypeSelector annotation = typePublisher.getClass().getAnnotation(SourceTypeSelector.class);
        if (annotation == null) {
            return false; // 어노테이션이 없는 경우 매칭하지 않음
        }
        SourceType[] types = annotation.sourceType();
        for (SourceType type : types) {
            if (type == sourceType) {
                return true; // 어노테이션에 정의된 타입과 일치하는 경우 매칭
            }
        }
        // 어노테이션에 정의된 타입과 일치하지 않는 경우 매칭하지 않음
        return false;
    }


}
