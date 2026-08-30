package com.brunosong.transfer.system.transfer.service.dataaccess.transfer.adapter;

import com.brunosong.transfer.system.outbox.OutboxStatus;
import com.brunosong.transfer.system.saga.SagaStatus;
import com.brunosong.transfer.system.transfer.service.dataaccess.TestJpaConfiguration;
import com.brunosong.transfer.system.transfer.service.dataaccess.transfer.mapper.TransferDataAccessMapperImpl;
import com.brunosong.transfer.system.transfer.service.dataaccess.transfer.repository.TransferOutboxJpaRepository;
import com.brunosong.transfer.system.transfer.service.outbox.model.DataTransferOutboxMessage;
import com.brunosong.transfer.system.transfer.service.valueobject.TransType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.brunosong.transfer.system.saga.transfer.SagaConstants.TRANSFER_SAGA_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// 매퍼는 MapStruct 가 만든 구현체를 올려야 한다. 인터페이스를 적으면 빈이 없다
@ContextConfiguration(classes = { TestJpaConfiguration.class , DataTransferOutboxRepositoryImpl.class ,
                                    TransferOutboxJpaRepository.class, TransferDataAccessMapperImpl.class })
@TestPropertySource(properties = {"spring.jpa.show-sql=true"})
@DataJpaTest
class DataTransferOutboxRepositoryImplTest {

    @Autowired
    private DataTransferOutboxRepositoryImpl dataTransferOutboxRepository;

    @Test
    void save() {
        DataTransferOutboxMessage saved = dataTransferOutboxRepository.save(outboxMessage(UUID.randomUUID()));

        assertEquals(TRANSFER_SAGA_NAME, saved.getType());
        assertEquals(OutboxStatus.STARTED, saved.getOutboxStatus());
        assertEquals(TransType.MESSAGING, saved.getTransType());
    }

    /** 스케줄러가 집어 가는 조건 그대로 찾아본다 */
    @Test
    void findByTypeAndOutboxStatusAndSagaStatus() {
        UUID sagaId = UUID.randomUUID();
        dataTransferOutboxRepository.save(outboxMessage(sagaId));

        Optional<List<DataTransferOutboxMessage>> found =
                dataTransferOutboxRepository.findByTypeAndOutboxStatusAndSagaStatus(TRANSFER_SAGA_NAME,
                        OutboxStatus.STARTED, SagaStatus.STARTED, SagaStatus.COMPENSATED);

        assertTrue(found.isPresent());
        assertEquals(1, found.get().size());
        assertEquals(sagaId, found.get().get(0).getSagaId());
    }

    /** 이미 나간 건은 다음 주기에 다시 집히면 안 된다 */
    @Test
    void completedMessageIsNotPickedUpAgain() {
        DataTransferOutboxMessage message = outboxMessage(UUID.randomUUID());
        message.setOutboxStatus(OutboxStatus.COMPLETED);
        dataTransferOutboxRepository.save(message);

        Optional<List<DataTransferOutboxMessage>> found =
                dataTransferOutboxRepository.findByTypeAndOutboxStatusAndSagaStatus(TRANSFER_SAGA_NAME,
                        OutboxStatus.STARTED, SagaStatus.STARTED, SagaStatus.COMPENSATED);

        assertTrue(found.isEmpty() || found.get().isEmpty());
    }

    private DataTransferOutboxMessage outboxMessage(UUID sagaId) {
        return DataTransferOutboxMessage.builder()
                .id(UUID.randomUUID())
                .sagaId(sagaId)
                .type(TRANSFER_SAGA_NAME)
                .transType(TransType.MESSAGING)
                .payload("{\"transferId\":\"" + UUID.randomUUID() + "\"}")
                .createdAt(ZonedDateTime.now(ZoneId.of("UTC")))
                .sagaStatus(SagaStatus.STARTED)
                .outboxStatus(OutboxStatus.STARTED)
                .build();
    }
}
