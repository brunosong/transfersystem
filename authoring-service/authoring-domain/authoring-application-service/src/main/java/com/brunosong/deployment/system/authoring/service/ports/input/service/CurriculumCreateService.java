package com.brunosong.deployment.system.authoring.service.ports.input.service;

import com.brunosong.deployment.system.authoring.service.entity.SkeletonDraft;

/** 커리큘럼 만들기(입력 포트). 번호를 받아 빈 뼈대 초안을 만든다 */
public interface CurriculumCreateService {

    SkeletonDraft create(String title, String description);
}
