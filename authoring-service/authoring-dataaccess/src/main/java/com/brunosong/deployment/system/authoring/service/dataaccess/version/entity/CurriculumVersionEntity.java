package com.brunosong.deployment.system.authoring.service.dataaccess.version.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

/**
 * 창고(curriculum_versions)의 문서 하나 = 판 하나. 한 번 넣으면 고치지 않는다.
 * _id 는 "CUR003:2" 처럼 커리큘럼:판 번호라, 같은 판 번호를 두 번 넣으면 Mongo 가 막는다
 */
@Document("curriculum_versions")
public record CurriculumVersionEntity(@Id String id, String curriculumId, int version, Instant publishedAt,
                                      BasedOnNode basedOn, CurriculumNode curriculum) {

    public static String idOf(String curriculumId, int version) {
        return curriculumId + ":" + version;
    }

    public record BasedOnNode(int skeletonRevision, Map<String, Integer> subjectRevisions) {
    }
}
