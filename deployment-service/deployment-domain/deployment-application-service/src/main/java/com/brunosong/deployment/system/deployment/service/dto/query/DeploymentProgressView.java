package com.brunosong.deployment.system.deployment.service.dto.query;

/**
 * 청크 진행률. total 이 0 이면 아직 몇 개로 나뉘는지 모르는 상태다.
 */
public record DeploymentProgressView(int total, long succeeded, long failed) {

    public long pending() {
        return Math.max(0, total - succeeded - failed);
    }
}
