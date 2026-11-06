package com.brunosong.deployment.system.deployment.service.dto.query;

/**
 * 원본 저장소 설정. 접속 계정과 비밀번호는 화면에 내보내지 않는다.
 */
public record SourceConfigView(
        Long sourceConfigId,
        String sourceType,
        String dbType,
        String host,
        Integer port,
        String database,
        String tableOrCollection) {
}
