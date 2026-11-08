package com.brunosong.deployment.system.domain.valueobject;

public enum DeploymentStatus {
    PENDING("Pending"),           // 초기 상태 (보내기 전)
    SENT("Sent"),                 // 보낸 상태 (API 요청 보냄 or Kafka로 메시지 발행)
    PROCESSED("Processed"),       // 처리 완료 상태 (API 응답 받음 or Messaging 처리 확인)
    SUCCESS("Success"),           // 저장까지 완료된 최종 성공 상태
    IN_PROGRESS("In progress"),   // 응답을 기다리는 청크가 있다
    SUCCEEDED("Succeeded"),       // 모든 청크가 대상에 반영됐다
    FAILED("Failed");             // 실패 상태 (어떤 단계에서든 오류 발생)

    private final String description;

    DeploymentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
