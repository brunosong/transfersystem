package com.brunosong.deployment.system.targetagent.service.ports.output.repository;

import com.brunosong.deployment.system.targetagent.service.domain.valueobject.ChunkContent;
import com.brunosong.deployment.system.targetagent.service.dto.message.ApplyOutcome;

public interface CurriculumRepository {

    ApplyOutcome saveOrUpdate(String curriculumId, ChunkContent content);
}
