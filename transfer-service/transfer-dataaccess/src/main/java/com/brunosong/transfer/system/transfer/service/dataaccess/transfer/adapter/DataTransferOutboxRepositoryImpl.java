package com.brunosong.transfer.system.transfer.service.dataaccess.transfer.adapter;

import com.brunosong.transfer.system.outbox.OutboxStatus;
import com.brunosong.transfer.system.saga.SagaStatus;
import com.brunosong.transfer.system.transfer.service.dataaccess.transfer.entity.TransferOutboxEntity;
import com.brunosong.transfer.system.transfer.service.dataaccess.transfer.mapper.TransferDataAccessMapper;
import com.brunosong.transfer.system.transfer.service.dataaccess.transfer.repository.TransferOutboxJpaRepository;
import com.brunosong.transfer.system.transfer.service.outbox.model.DataTransferOutboxMessage;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.DataTransferOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataTransferOutboxRepositoryImpl implements DataTransferOutboxRepository {

    private final TransferOutboxJpaRepository transferOutboxJpaRepository;
    private final TransferDataAccessMapper transferDataAccessMapper;

    @Override
    public DataTransferOutboxMessage save(DataTransferOutboxMessage dataTransferOutboxMessage) {
        TransferOutboxEntity transferOutboxEntity = transferDataAccessMapper.toTransferOutboxEntity(dataTransferOutboxMessage);
        TransferOutboxEntity saveEntity = transferOutboxJpaRepository.save(transferOutboxEntity);
        return transferDataAccessMapper.toDataTransferOutboxMessage(saveEntity);
    }

    @Override
    public Optional<List<DataTransferOutboxMessage>> findByTypeAndOutboxStatusAndSagaStatus(String type,
                                                                                            OutboxStatus outboxStatus,
                                                                                            SagaStatus... sagaStatus) {
        // Implementation logic to find messages by type, outbox status, and saga status
        return Optional.empty(); // Replace with actual implementation
    }

    @Override
    public Optional<DataTransferOutboxMessage> findByTypeSagaIdAndSagaStatus(String type, UUID sagaId,
                                                                             SagaStatus... sagaStatus) {
        // Implementation logic to find a message by type, saga ID, and saga status
        return Optional.empty(); // Replace with actual implementation
    }

    @Override
    public void deleteByTypeAndOutboxStatusAndSagaStatus(String type,
                                                         OutboxStatus outboxStatus,
                                                         SagaStatus... sagaStatus) {
        // Implementation logic to delete messages by type, outbox status, and saga status
    }
}
