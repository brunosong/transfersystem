package com.brunosong.deployment.system.domain.valueobject;

/**
 * 배포 상태. 대상이 알려 준 결과를 보고 배포가 정한다(docs/domain/model.md 4장).
 * 옛 처리 단계 값(PENDING, SENT, PROCESSED, SUCCESS)은 지웠다. 남아 있던 행은 V13 이 바꿨다.
 */
public enum DeploymentStatus {
    IN_PROGRESS("In progress"),   // 응답을 기다리는 청크가 있다
    SUCCEEDED("Succeeded"),       // 모든 청크가 대상에 반영됐다
    FAILED("Failed");             // 기다릴 청크가 없는데 반영되지 않은 청크가 있다

    private final String description;

    DeploymentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
