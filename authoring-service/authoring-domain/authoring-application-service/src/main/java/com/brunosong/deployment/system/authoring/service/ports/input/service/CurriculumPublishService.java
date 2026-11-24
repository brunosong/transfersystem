package com.brunosong.deployment.system.authoring.service.ports.input.service;

import com.brunosong.deployment.system.authoring.service.entity.CurriculumVersion;

import java.util.Map;

/**
 * 공개(입력 포트). 초안(뼈대 1개 + 과목 N개)을 모아 판 하나를 만들고, 배포가 읽는 곳에 놓는다.
 * revision 들은 화면이 공개 화면을 열 때 본 값이다. 하나라도 지금과 다르면 RevisionConflictException(409)
 */
public interface CurriculumPublishService {

    /**
     * 공개한다. 바뀐 것이 없거나 초안이 어긋나면 PublishRejectedException(422).
     * 둘이 동시에 공개해서 판 번호가 겹치면 늦은 쪽은 409
     */
    CurriculumVersion publish(String curriculumId, int skeletonRevision, Map<String, Integer> subjectRevisions);
}
