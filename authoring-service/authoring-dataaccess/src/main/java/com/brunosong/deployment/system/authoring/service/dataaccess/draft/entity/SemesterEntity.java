package com.brunosong.deployment.system.authoring.service.dataaccess.draft.entity;

/** 학년 안에 들어 있는 학기 하나. 따로 컬렉션을 두지 않는다 */
public record SemesterEntity(String id, String title, String description) {}
