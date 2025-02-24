package com.brunosong.transfer.system.transfer.service.valueobject;

public enum TransferStatus {
    PENDING("Pending"),           // 초기 상태 (전송 전)
    SENT("Sent"),                 // 전송 완료 상태 (API 요청 보냄 or Kafka로 메시지 발행)
    PROCESSED("Processed"),       // 처리 완료 상태 (API 응답 받음 or Messaging 처리 확인)
    SUCCESS("Success"),           // 저장까지 완료된 최종 성공 상태
    FAILED("Failed");             // 실패 상태 (어떤 단계에서든 오류 발생)

    private final String description;

    TransferStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
