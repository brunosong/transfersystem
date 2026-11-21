package com.brunosong.deployment.system.authoring.service.dataaccess.draft.entity;

import java.util.List;

/** 뼈대 초안 문서 안에 들어 있는 학년 하나. 학기를 담는다. 따로 컬렉션을 두지 않는다 */
public record GradeEntity(String id, String title, String description, List<SemesterEntity> semesters) {}
