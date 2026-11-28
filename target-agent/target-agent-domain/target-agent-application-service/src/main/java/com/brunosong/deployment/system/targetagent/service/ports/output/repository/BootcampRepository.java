package com.brunosong.deployment.system.targetagent.service.ports.output.repository;

import com.brunosong.deployment.system.targetagent.service.domain.valueobject.AppliedContent;
import com.brunosong.deployment.system.targetagent.service.domain.valueobject.ChunkContent;

/** 부트캠프 대상 DB에 청크 하나를 반영한다. 어느 DB(PROD/DEV)에 쓸지는 부르기 전에 라우팅 키로 정해 둔다 */
public interface BootcampRepository {

    AppliedContent saveOrUpdate(String curriculumId, ChunkContent content);
}
