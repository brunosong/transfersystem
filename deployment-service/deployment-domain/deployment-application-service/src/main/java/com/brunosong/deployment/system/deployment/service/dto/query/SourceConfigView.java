package com.brunosong.deployment.system.deployment.service.dto.query;

/**
 * 원본 저장소 설정. 접속 계정과 비밀번호는 화면에 내보내지 않는다.
 * name 과 enabled 는 화면이 원본을 고를 때 쓴다. 표에 칸이 없어서 지금은 만들어 준다:
 * name 은 "데이터베이스.컬렉션", enabled 는 늘 true 다.
 */
public record SourceConfigView(
        Long sourceConfigId,
        String name,
        boolean enabled,
        String sourceType,
        String dbType,
        String host,
        Integer port,
        String database,
        String tableOrCollection) {
}
