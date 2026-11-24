package com.brunosong.deployment.system.authoring.service.dataaccess.draft.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@AllArgsConstructor
@Document("skeleton_drafts")
public class SkeletonDraftEntity {

    // 커리큘럼의 ID를 문서의 _id로 쓴다. 커리큘럼마다 뼈대 초안은 하나라서 겹치지 않는다.
    @Id
    private String curriculumId;

    private String title;
    private String description;

    // 커리큘럼끼리의 정렬 순서. 이 칸이 생기기 전에 만든 문서는 null 이다
    private Integer sortOrder;

    private List<GradeEntity> grades;

    // 낙관적 락에 쓰는 번호. 고치기 기능에서 조건부 갱신의 조건이 된다.
    private int revision;
}
