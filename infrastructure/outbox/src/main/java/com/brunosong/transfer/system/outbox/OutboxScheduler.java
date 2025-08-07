package com.brunosong.transfer.system.outbox;

public interface OutboxScheduler {
    void processOutboxMessages();
}
