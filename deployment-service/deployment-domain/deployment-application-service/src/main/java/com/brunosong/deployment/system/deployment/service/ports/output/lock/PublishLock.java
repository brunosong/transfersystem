package com.brunosong.deployment.system.deployment.service.ports.output.lock;

/**
 * 여러 인스턴스 중 한 대만 아웃박스를 발행하게 하는 락.
 * 두 대가 같은 키의 행을 나눠 보내면 순서가 뒤집힐 수 있어서 한 번에 한 대만 돌린다(ADR 0004).
 */
public interface PublishLock {

    /**
     * 다른 인스턴스가 락을 쥐고 있지 않으면 락을 잡고 task 를 돌린 뒤 풀고 true 를 돌려준다.
     * 쥐고 있으면 task 를 돌리지 않고 바로 false 를 돌려준다. 기다리지 않는다.
     * task 가 예외를 던져도 락은 푼다.
     */
    boolean runIfFree(Runnable task);
}