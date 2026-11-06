package com.brunosong.deployment.system.deployment.service.valueobject;

public enum FailureKind {
    REJECTED,   // 대상이 실패를 알렸다.
    NO_REPLY    // 기한 안에 응답이 없었다. 반영됐을 수도 있다.
}
