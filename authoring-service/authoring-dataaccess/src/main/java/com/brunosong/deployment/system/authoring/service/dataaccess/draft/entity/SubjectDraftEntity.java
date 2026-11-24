package com.brunosong.deployment.system.authoring.service.dataaccess.draft.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;


@Getter
@AllArgsConstructor
@Document("subject_drafts")
public class SubjectDraftEntity {

    @Id
    private String id;

    // 커리큘럼의 과목을 모아 읽을 때 쓴다. 과목이 많아도 이 칸으로 빨리 찾게 인덱스를 둔다
    @Indexed
    private String curriculumId;

    private String semesterId;
    private String title;
    private String description;
    // 한 학기 안의 과목끼리의 정렬 순서. 이 칸이 생기기 전에 만든 문서는 null 이다
    private Integer sortOrder;

    private List<UnitEntity> units;

    private int revision;
}
