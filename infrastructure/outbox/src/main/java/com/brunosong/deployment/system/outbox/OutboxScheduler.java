package com.brunosong.deployment.system.outbox;

public interface OutboxScheduler {
    void processOutboxMessages();
}
