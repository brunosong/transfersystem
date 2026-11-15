package com.brunosong.deployment.system.deployment.service.ports.output.message.publisher;

/**
 * 아웃박스 행 하나를 보낸 결과. 어떤 실패가 다시 보낼 만한지는 어댑터가 판단하고,
 * 스케줄러는 이 세 값만 보고 행의 상태를 정한다. 그래서 스케줄러는 Kafka 를 모른다.
 */
public enum PublishResult {
    /** 브로커가 받았다. 행을 닫는다 */
    PUBLISHED,
    /** 이번에는 못 보냈지만 다시 보내면 나갈 수 있다(브로커 응답 없음, 시간 초과 등). 행을 그대로 두고 다음 주기에 다시 보낸다 */
    RETRY_LATER,
    /** 다시 보내도 결과가 같다(페이로드를 못 읽음, 메시지가 너무 큼 등). 행을 실패로 닫는다 */
    GIVE_UP
}
