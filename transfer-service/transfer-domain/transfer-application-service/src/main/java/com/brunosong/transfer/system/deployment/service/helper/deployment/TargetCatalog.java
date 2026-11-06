package com.brunosong.transfer.system.deployment.service.helper.deployment;

import com.brunosong.transfer.system.deployment.service.exception.DeploymentDomainException;

import java.util.Map;

/**
 * 요청의 숫자 대상 ID 를 계약의 시스템, 환경 이름으로 바꾼다.
 *
 * 임시 코드다. 대상 목록은 아직 대상 에이전트의 datamigrations 표에 있고, ID 는 그 표의 시드 순서와 같다.
 * 대상 목록을 배포 컨텍스트로 옮기면 지운다 (ADR 0002 이행 2단계).
 */
public final class TargetCatalog {

    public record Target(String system, String environment) {
    }

    private static final Map<Long, Target> TARGETS = Map.of(
            1L, new Target("ONLINE_CAMPUS", "PROD"),
            2L, new Target("BOOTCAMP", "PROD"),
            3L, new Target("ONLINE_CAMPUS", "DEV"),
            4L, new Target("BOOTCAMP", "DEV"));

    private TargetCatalog() {
    }

    public static Target find(long targetId) {
        Target target = TARGETS.get(targetId);
        if (target == null) {
            throw new DeploymentDomainException("모르는 대상입니다: " + targetId);
        }
        return target;
    }
}
