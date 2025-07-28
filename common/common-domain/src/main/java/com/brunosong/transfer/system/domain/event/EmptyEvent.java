package com.brunosong.transfer.system.domain.event;

// 마커 클래스
public final class EmptyEvent implements DomainEvent<Void> {

    public static EmptyEvent emptyEvent = new EmptyEvent();

    private EmptyEvent() {}

    @Override
    public void fire() {

    }
}
