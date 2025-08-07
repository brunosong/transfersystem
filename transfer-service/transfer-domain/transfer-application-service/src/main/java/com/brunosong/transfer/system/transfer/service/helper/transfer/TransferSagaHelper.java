package com.brunosong.transfer.system.transfer.service.helper.transfer;

import com.brunosong.transfer.system.domain.valueobject.TransferStatus;
import com.brunosong.transfer.system.saga.SagaStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class TransferSagaHelper {

    SagaStatus transferStatusToSagaStatus(TransferStatus transferStatus) {
        switch (transferStatus) {
            case PENDING:
                return SagaStatus.PROCESSING;
            case SENT:
                return SagaStatus.COMPENSATED;
            case SUCCESS:
                return SagaStatus.SUCCEEDED;
            case FAILED:
                return SagaStatus.FAILED;
            default:
                return SagaStatus.STARTED;
        }
    }
}
