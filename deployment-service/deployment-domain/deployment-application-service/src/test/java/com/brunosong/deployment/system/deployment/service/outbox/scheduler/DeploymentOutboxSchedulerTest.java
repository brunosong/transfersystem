package com.brunosong.deployment.system.deployment.service.outbox.scheduler;

import com.brunosong.deployment.system.deployment.service.outbox.model.DeploymentOutboxMessage;
import com.brunosong.deployment.system.deployment.service.ports.output.lock.PublishLock;
import com.brunosong.deployment.system.deployment.service.ports.output.message.publisher.PublishResult;
import com.brunosong.deployment.system.deployment.service.valueobject.TransType;
import com.brunosong.deployment.system.outbox.OutboxStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class DeploymentOutboxSchedulerTest {

    private final DeploymentOutboxHelper outboxHelper = mock(DeploymentOutboxHelper.class);
    private final DeploymentOutboxPublisherHelper publisherHelper = mock(DeploymentOutboxPublisherHelper.class);
    // 가짜 락: 늘 잡히고, 받은 작업을 그 자리에서 돌린다. 락이 실제로 막는지는 PostgresPublishLockTest 가 본다
    private final PublishLock alwaysFree = task -> {
        task.run();
        return true;
    };
    private final DeploymentOutboxScheduler scheduler = new DeploymentOutboxScheduler(outboxHelper, publisherHelper, alwaysFree);

    @Test
    @DisplayName("같은 키의 앞 행을 못 보내면 뒤 행은 건너뛰고, 다른 키는 그대로 보낸다")
    void retryLaterBlocksOnlySameKey() {
        DeploymentOutboxMessage a1 = row("ONLINE_CAMPUS:PROD:CUR01");
        DeploymentOutboxMessage a2 = row("ONLINE_CAMPUS:PROD:CUR01");
        DeploymentOutboxMessage b1 = row("ONLINE_CAMPUS:PROD:CUR02");
        rowsToSend(a1, a2, b1);
        when(publisherHelper.publish(a1)).thenReturn(PublishResult.RETRY_LATER);
        when(publisherHelper.publish(b1)).thenReturn(PublishResult.PUBLISHED);

        scheduler.processOutboxMessages();

        verify(publisherHelper, never()).publish(a2);
        assertThat(a1.getOutboxStatus()).isEqualTo(OutboxStatus.STARTED);
        assertThat(a2.getOutboxStatus()).isEqualTo(OutboxStatus.STARTED);
        assertThat(b1.getOutboxStatus()).isEqualTo(OutboxStatus.COMPLETED);
        assertThat(b1.getProcessedAt()).isNotNull();
        verify(outboxHelper, never()).save(a1);

    }

    @Test
    @DisplayName("보낸 행은 COMPLETED, 포기한 행은 FAILED 로 닫는다. 포기한 행은 같은 키의 뒤 행을 막지 않는다")
    void publishedAndGiveUpCloseRows() {
        DeploymentOutboxMessage a1 = row("ONLINE_CAMPUS:PROD:CUR01");
        DeploymentOutboxMessage a2 = row("ONLINE_CAMPUS:PROD:CUR01");
        rowsToSend(a1, a2);
        when(publisherHelper.publish(a1)).thenReturn(PublishResult.GIVE_UP);
        when(publisherHelper.publish(a2)).thenReturn(PublishResult.PUBLISHED);

        scheduler.processOutboxMessages();

        assertThat(a1.getOutboxStatus()).isEqualTo(OutboxStatus.FAILED);
        assertThat(a2.getOutboxStatus()).isEqualTo(OutboxStatus.COMPLETED);
    }

    @Test
    @DisplayName("읽은 순서(seq)대로 하나씩 보낸다")
    void sendsInReadOrder() {
        DeploymentOutboxMessage a1 = row("ONLINE_CAMPUS:PROD:CUR01");
        DeploymentOutboxMessage b1 = row("ONLINE_CAMPUS:PROD:CUR02");
        DeploymentOutboxMessage a2 = row("ONLINE_CAMPUS:PROD:CUR01");
        rowsToSend(a1, b1, a2);
        when(publisherHelper.publish(any())).thenReturn(PublishResult.PUBLISHED);

        scheduler.processOutboxMessages();

        InOrder order = inOrder(publisherHelper);
        order.verify(publisherHelper).publish(a1);
        order.verify(publisherHelper).publish(b1);
        order.verify(publisherHelper).publish(a2);
    }

    /**
     * 확인하는 것: 다른 인스턴스가 락을 쥐고 있으면 이번 주기에는 아무것도 하지 않는다.
     *
     * 락을 못 잡았는데 행을 읽거나 보내면, 두 인스턴스가 같은 행을 보내 메시지가 두 번 나가고
     * 같은 키의 행이 두 곳에서 나가 순서가 뒤집힐 수 있다.
     */
    @Test
    @DisplayName("다른 인스턴스가 발행 중이면 행을 읽지도 보내지도 않는다")
    void skipsWhenAnotherInstanceHoldsLock() {
        // 준비: 락이 늘 잡혀 있는 상황을 흉내 낸다(작업을 돌리지 않고 false)
        DeploymentOutboxScheduler other = new DeploymentOutboxScheduler(outboxHelper, publisherHelper, task -> false);

        // 실행
        other.processOutboxMessages();

        // 확인: 아웃박스 표를 읽지도 않았고(행 조회 없음), Kafka 로 보내지도 않았다
        verifyNoInteractions(outboxHelper, publisherHelper);
    }

    private void rowsToSend(DeploymentOutboxMessage... rows) {
        when(outboxHelper.getDeploymentOutboxMessageByOutboxStatus(OutboxStatus.STARTED))
                .thenReturn(Optional.of(List.of(rows)));
    }

    private static DeploymentOutboxMessage row(String messageKey) {
        return DeploymentOutboxMessage.builder()
                .id(UUID.randomUUID())
                .transType(TransType.MESSAGING)
                .messageKey(messageKey)
                .outboxStatus(OutboxStatus.STARTED)
                .build();
    }

}