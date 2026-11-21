package com.brunosong.deployment.system.authoring.service.dataaccess.draft.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@AllArgsConstructor
@Document("skeleton_drafts")
public class SkeletonDraftEntity {

    // 커리큘럼의 ID를 문서의 _id로 쓴다. 커리큘럼마다 뼈대 초안은 하나라서 겹치지 않는다.
    @Id
    private String curriculumId;

    private String title;
    private String description;

    // 낙관적 락에 쓰는 번호. 고치기 기능에서 조건부 갱신의 조건이 된다.
    private int revision;
}
