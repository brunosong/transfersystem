package com.brunosong.deployment.system.authoring.service.dataaccess.draft.entity;

/** 뼈대 초안 문서 안에 들어 있는 학년 하나. 따로 컬렉션을 두지 않는다 */
public record GradeEntity(String id, String title, String description) {}
