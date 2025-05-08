package com.brunosong.transfer.system.transfer.service.cache.transfer.adapter;

import com.brunosong.transfer.system.transfer.service.cache.transfer.entity.TransferCacheEntity;
import com.brunosong.transfer.system.transfer.service.cache.transfer.mapper.TransferLogCacheMapper;
import com.brunosong.transfer.system.transfer.service.cache.transfer.repository.TransferLogRedisRepository;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.TransferLogCachePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransferLogCacheAdapter implements TransferLogCachePort {

    private final TransferLogCacheMapper transferLogCacheMapper;
    private final TransferLogRedisRepository transferLogRedisRepository;

    @Override
    public Optional<Transfer> getTransferInfo(String transferId) {
        log.info("Checking Redis cache for transferId: {}", transferId);
        return transferLogRedisRepository.findById(transferId)
                .map(transferLogCacheMapper::toTransfer);
    }

    @Override
    public void saveTransferId(String transferId, int totalChunkSize) {
        log.info("Saving to Redis cache for transferId: {}", transferId);
        TransferCacheEntity entity = TransferCacheEntity.builder()
                .transferId(transferId)
                .totalChunkSize(totalChunkSize)
                .build();
        transferLogRedisRepository.save(entity);
    }
}
