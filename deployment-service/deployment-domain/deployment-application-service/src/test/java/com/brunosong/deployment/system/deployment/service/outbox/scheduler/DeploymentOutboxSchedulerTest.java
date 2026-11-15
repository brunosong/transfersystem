package com.brunosong.deployment.system.deployment.service.outbox.scheduler;

import com.brunosong.deployment.system.deployment.service.outbox.model.DeploymentOutboxMessage;
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
    private final DeploymentOutboxScheduler scheduler = new DeploymentOutboxScheduler(outboxHelper, publisherHelper);

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