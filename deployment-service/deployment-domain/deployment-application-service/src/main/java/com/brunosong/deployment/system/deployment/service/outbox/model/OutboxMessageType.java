package com.brunosong.deployment.system.deployment.service.outbox.model;

/**
 * 아웃박스 행의 type 칸에 적는 메시지 종류.
 *
 * 한 아웃박스 표에 여러 종류의 메시지가 쌓여도 같은 경로로 보내려고 둔다(ADR 0004).
 * 지금은 청크 반영 명령 하나뿐이다. 값은 메시지 계약의 이름과 같게 둔다.
 * 전에는 사가 이름(deploymentProcessingSaga)을 적었는데, 사가는 쓰지 않기로 해서 메시지 이름으로 바꿨다.
 */
public final class OutboxMessageType {

    /** 대상에 청크 하나를 반영해 달라는 명령 (ApplyChunkCommand) */
    public static final String APPLY_CHUNK_COMMAND = "ApplyChunkCommand";

    private OutboxMessageType() {
    }
}
