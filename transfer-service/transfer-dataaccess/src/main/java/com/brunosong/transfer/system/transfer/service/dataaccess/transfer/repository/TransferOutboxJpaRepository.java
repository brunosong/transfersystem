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

    // 세 메서드 모두 init-schema 에 만들어 둔 (type, outbox_status, saga_status) 인덱스를 탄다.
    // 포트가 SagaStatus 를 가변인자로 받으므로 여기서는 In 으로 받는다.
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
