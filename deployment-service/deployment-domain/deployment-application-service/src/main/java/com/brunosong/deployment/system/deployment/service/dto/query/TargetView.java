package com.brunosong.deployment.system.deployment.service.dto.query;

/**
 * 화면의 대상 목록 한 줄. 대상 목록은 아직 임시 목록(TargetCatalog)에 있다.
 * 접속 주소와 마지막 연결 확인 결과는 그 목록에 없어서 싣지 않는다. 화면은 값이 없으면 비워서 보여 준다.
 * 대상을 끄는 기능이 없어서 enabled 는 늘 true 다.
 */
public record TargetView(Long targetId, String system, String environment, boolean enabled) {
}
