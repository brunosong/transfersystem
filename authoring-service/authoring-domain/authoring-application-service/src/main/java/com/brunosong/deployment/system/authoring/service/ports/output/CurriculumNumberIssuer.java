package com.brunosong.deployment.system.authoring.service.ports.output;

/** 커리큘럼 번호표, 부를 때마다 1,2,3, ..을 겹치지 않게 내준다. */
public interface CurriculumNumberIssuer {
    long next();
}
