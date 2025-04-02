package com.brunosong.transfer.system.datamigration.service.helper;

import com.brunosong.transfer.system.datamigration.service.dto.message.DataMigrationRequest;
import com.brunosong.transfer.system.datamigration.service.dto.message.DataMigrationResponseMessage;
import com.brunosong.transfer.system.datamigration.service.ports.output.message.publisher.transfer.DataMigrationResponsePublisher;
import com.brunosong.transfer.system.domain.valueobject.TransferStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageResponseHelper {

    private final DataMigrationResponsePublisher dataMigrationResponsePublisher;

    public void failsResponseMessage(DataMigrationRequest dataMigrationRequest, Exception e) {

        log.error("migration exception! transferId is {}, dataMigrationId : {}",
                dataMigrationRequest.getTransferId(), dataMigrationRequest.getDataMigrationId());

        // DLT 대신 처리 실패 메시지 전송
        DataMigrationResponseMessage outboxMessage = DataMigrationResponseMessage.builder()
                .transferId(dataMigrationRequest.getTransferId())
                .message("ErrorMessage is " + e.getMessage())
                .transferStatus(TransferStatus.FAILED)
                .build();

        dataMigrationResponsePublisher.dataMigrationStatusPublish(outboxMessage);

    }

    public void successResponseMessage(DataMigrationRequest dataMigrationRequest) {

        log.info("migration success !! transferId is {}, dataMigrationId : {}",
                dataMigrationRequest.getTransferId(), dataMigrationRequest.getDataMigrationId());

        // 성공 시 메시지
        DataMigrationResponseMessage outboxMessage = DataMigrationResponseMessage.builder()
                .transferId(dataMigrationRequest.getTransferId())
                .message("SUCCESS")
                .transferStatus(TransferStatus.SUCCESS)
                .build();

        dataMigrationResponsePublisher.dataMigrationStatusPublish(outboxMessage);
    }

}
