package com.brunosong.deployment.system.targetagent.service.ports.output.cache;

public interface CurriculumCachePort {
    void save(long id, String curriculumId);

    long findById(String curriculumId);
}
