package com.brunosong.deployment.system.deployment.service.ports.input.service;

import com.brunosong.deployment.system.deployment.service.dto.query.CurriculumSummaryView;
import com.brunosong.deployment.system.deployment.service.dto.query.CurriculumTreeView;
import com.brunosong.deployment.system.deployment.service.dto.query.PageView;

/** 화면(콘솔)이 배포할 커리큘럼을 고를 때 쓰는 조회. 원본 저장소를 읽기만 한다 */
public interface CurriculumQueryService {

    /** 원본 저장소의 커리큘럼 목록. keyword 는 커리큘럼 ID나 제목, page 는 0부터 센다 */
    PageView<CurriculumSummaryView> findCurriculums(Long sourceConfigId, String keyword, int page, int size);

    /** 커리큘럼 하나의 트리와 개수, 배포하면 나뉘는 청크 수 */
    CurriculumTreeView findCurriculum(Long sourceConfigId, String curriculumId);
}
