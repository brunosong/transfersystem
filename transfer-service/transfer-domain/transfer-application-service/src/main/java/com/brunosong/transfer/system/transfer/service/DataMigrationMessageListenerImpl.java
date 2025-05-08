package com.brunosong.transfer.system.transfer.service;

import com.brunosong.transfer.system.transfer.service.dto.create.DataMigrationResponse;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.exception.TransferLogNotFoundException;
import com.brunosong.transfer.system.transfer.service.mapper.TransferDataMapper;
import com.brunosong.transfer.system.transfer.service.ports.input.message.listener.DataMigrationMessageListener;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.TransferLogCachePort;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.TransferLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataMigrationMessageListenerImpl implements DataMigrationMessageListener {

    private final TransferLogRepository transferLogRepository;
    private final TransferDataMapper transferDataMapper;
    private final TransferLogCachePort transferLogCachePort;

    @Override
    @Transactional
    public void transferStatusUpdate(DataMigrationResponse dataMigrationResponse) {
        Transfer transfer = transferDataMapper.dataMigrationResponseToTransfer(dataMigrationResponse);

        if (dataMigrationResponse.getChunkOffset() == 0) {
            transferLogRepository.updateTransferStatus(transfer);

            Transfer result = transferLogRepository.findById(transfer.getId().getValue()).orElseThrow(() -> {
                throw new TransferLogNotFoundException("transferId is " + transfer.getId().getValue());
            });

            transferLogCachePort.saveTransferId(transfer.getId().getValue().toString(),
                                                result.getTotalChunkSize());
        } else {
            Optional<Transfer> transferInfo =
                    transferLogCachePort.getTransferInfo(transfer.getId().getValue().toString());

            // 캐시 미스 시 DB 조회
            if (transferInfo.isEmpty()) {
                transferInfo = transferLogRepository.findById(transfer.getId().getValue());
            }

            dataMigrationResponse.updateTotalChunkSize(transferInfo.get().getTotalChunkSize());
            transferLogRepository.saveChunk(dataMigrationResponse);
        }
    }
}
