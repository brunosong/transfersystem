package com.brunosong.deployment.system.deployment.service.ports.output.repository;

import com.brunosong.deployment.system.deployment.service.valueobject.SourceType;

/**
 * 원본 저장소에서 꺼낸 문서 그대로. 도메인 객체가 아니라 포트가 돌려주는 데이터다.
 * json 은 원본마다 다른 방법으로 만든 JSON 바이트이고, 해석은 변환기가 한다.
 * byte[] 라 equals 가 내용을 비교하지 않지만, 비교할 일이 없어서 그대로 둔다.
 */
public record SourceDocument(String sourceId, SourceType sourceType, byte[] json) {
}
