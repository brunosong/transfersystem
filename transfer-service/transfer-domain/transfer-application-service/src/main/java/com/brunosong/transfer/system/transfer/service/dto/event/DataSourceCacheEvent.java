package com.brunosong.transfer.system.transfer.service.dto.event;

import org.springframework.context.ApplicationEvent;

public class DataSourceCacheEvent extends ApplicationEvent {

    private final EventType eventType;

    public DataSourceCacheEvent(Object source, EventType eventType) {
        super(source);
        this.eventType = eventType;
    }

    public EventType getEventType() {
        return eventType;
    }

    public enum EventType {
        DATA_SOURCE_CACHE_UPDATE,
        DATA_SOURCE_CACHE_DELETE
    }

}
