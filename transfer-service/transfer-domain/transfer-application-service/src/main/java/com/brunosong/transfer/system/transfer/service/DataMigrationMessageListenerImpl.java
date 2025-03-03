package com.brunosong.transfer.system.transfer.service;

import com.brunosong.transfer.system.transfer.service.dto.create.DataMigrationResponse;
import com.brunosong.transfer.system.transfer.service.ports.input.message.listener.DataMigrationMessageListener;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.TransferLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataMigrationMessageListenerImpl implements DataMigrationMessageListener {

    private final TransferLogRepository transferLogRepository;

    @Override
    public void transferStatusUpdate(DataMigrationResponse dataMigrationResponse) {
        transferLogRepository.updateTransferStatus(dataMigrationResponse.getTransferId(), dataMigrationResponse.getTransferStatus());
    }
}
