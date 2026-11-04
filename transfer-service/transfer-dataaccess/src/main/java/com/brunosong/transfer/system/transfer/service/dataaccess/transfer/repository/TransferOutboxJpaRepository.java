package com.brunosong.transfer.system.transfer.service.dataaccess.transfer.repository;

import com.brunosong.transfer.system.outbox.OutboxStatus;
import com.brunosong.transfer.system.saga.SagaStatus;
import com.brunosong.transfer.system.transfer.service.dataaccess.transfer.entity.TransferOutboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransferOutboxJpaRepository extends JpaRepository<TransferOutboxEntity, UUID> {

    Optional<List<TransferOutboxEntity>> findByTypeAndOutboxStatusAndSagaStatusIn(String type,
                                                                                  OutboxStatus outboxStatus,
                                                                                  List<SagaStatus> sagaStatus);

    Optional<TransferOutboxEntity> findByTypeAndSagaIdAndSagaStatusIn(String type,
                                                                      UUID sagaId,
                                                                      List<SagaStatus> sagaStatus);

    void deleteByTypeAndOutboxStatusAndSagaStatusIn(String type,
                                                    OutboxStatus outboxStatus,
                                                    List<SagaStatus> sagaStatus);
}
