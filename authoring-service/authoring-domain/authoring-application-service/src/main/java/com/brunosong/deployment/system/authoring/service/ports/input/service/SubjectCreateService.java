package com.brunosong.deployment.system.authoring.service.ports.input.service;

import com.brunosong.deployment.system.authoring.service.entity.SubjectDraft;

/** 과목 만들기(입력 포트). 학기가 뼈대에 없으면 SemesterNotFoundException(404) */
public interface SubjectCreateService {

    SubjectDraft create(String curriculumId, String semesterId, String title, String description);
}
