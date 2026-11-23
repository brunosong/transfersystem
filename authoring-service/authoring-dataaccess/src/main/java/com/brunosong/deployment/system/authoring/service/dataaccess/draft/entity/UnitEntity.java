package com.brunosong.deployment.system.authoring.service.dataaccess.draft.entity;

import java.util.List;

/** 과목 초안 문서 안에 들어 있는 단원 하나. 차시를 담는다. 따로 컬렉션을 두지 않는다 */
public record UnitEntity(String id, String title, String description, List<LessonEntity> lessons) {
}
