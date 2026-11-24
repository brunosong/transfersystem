package com.brunosong.deployment.system.authoring.service.ports.output.repository;

import com.brunosong.deployment.system.authoring.service.entity.CurriculumVersion;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 판 저장소. 판은 두 곳에 있다.
 * - curriculum_versions: 모든 판(기록용). 한 번 쓰면 고치지 않는다
 * - published_curricula: 커리큘럼마다 가장 최근 판 하나. 배포가 읽는 곳이다(ADR 0008)
 */
public interface CurriculumVersionRepository {

    /** 그 커리큘럼의 가장 최근 판. 공개한 적이 없으면 빈 값 */
    Optional<CurriculumVersion> findLast(String curriculumId);

    /** 그 커리큘럼의 판 기록. 최근 판이 먼저다. 내용(트리)은 읽지 않는다 */
    List<VersionSummary> findSummaries(String curriculumId);

    /** 커리큘럼마다 가장 최근 판 번호. 공개한 적이 없는 커리큘럼은 빠진다. 목록 화면에 쓴다 */
    Map<String, Integer> lastVersionNumbers(List<String> curriculumIds);

    /**
     * 판을 두 곳에 함께 쓴다. 둘 다 써지거나 둘 다 안 써진다(트랜잭션).
     * 같은 판 번호가 이미 있으면(둘이 동시에 공개했으면) 쓰지 않고 false
     */
    boolean publish(CurriculumVersion version);
}
