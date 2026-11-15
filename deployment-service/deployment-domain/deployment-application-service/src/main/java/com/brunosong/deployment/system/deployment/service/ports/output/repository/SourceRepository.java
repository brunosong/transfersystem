package com.brunosong.deployment.system.deployment.service.ports.output.repository;

import com.brunosong.deployment.system.deployment.service.entity.SourceConfig;

import java.util.Optional;

public interface SourceRepository {
    Optional<SourceDocument> findData(String sourceId, SourceConfig sourceConfig);

    /** 화면 목록용. 검색어(커리큘럼 ID나 제목)에 맞는 문서를 한 쪽 읽는다. page 는 0부터 센다 */
    SourceDocumentPage findPage(SourceConfig sourceConfig, String keyword, int page, int size);
}
