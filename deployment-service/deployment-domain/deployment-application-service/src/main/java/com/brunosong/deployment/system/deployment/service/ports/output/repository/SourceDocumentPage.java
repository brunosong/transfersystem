package com.brunosong.deployment.system.deployment.service.ports.output.repository;

import java.util.List;

/** 원본 문서 한 쪽과, 검색어에 맞는 전체 문서 수 */
public record SourceDocumentPage(List<SourceDocument> documents, long totalItems) {
}
