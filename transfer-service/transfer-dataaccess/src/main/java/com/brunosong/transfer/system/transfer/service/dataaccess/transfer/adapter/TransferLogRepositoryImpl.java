package com.brunosong.transfer.system.transfer.service.dataaccess.transfer.adapter;

import com.brunosong.transfer.system.transfer.service.dataaccess.transfer.entity.TransferLogEntity;
import com.brunosong.transfer.system.transfer.service.dataaccess.transfer.mapper.TransferLogDataAccessMapper;
import com.brunosong.transfer.system.transfer.service.dataaccess.transfer.repository.TransferLogJpaRepository;
import com.brunosong.transfer.system.transfer.service.entity.TransferLog;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.TransferLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransferLogRepositoryImpl implements TransferLogRepository {

    private final TransferLogJpaRepository transferLogJpaRepository;
    private final TransferLogDataAccessMapper transferLogDataAccessMapper;

    @Override
    public TransferLog save(TransferLog transferLog) {
        TransferLogEntity save = transferLogJpaRepository.save(transferLogDataAccessMapper.transferLogToTransferLogEntity(transferLog));
        return transferLogDataAccessMapper.transferLogEntityToTransferLog(save);
    }

}
