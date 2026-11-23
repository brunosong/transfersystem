package com.brunosong.deployment.system.authoring.service.dataaccess.draft.entity;

/** 단원 안에 들어 있는 차시 하나. 따로 컬렉션을 두지 않는다 */
public record LessonEntity(String id, String title, String description) {
}
