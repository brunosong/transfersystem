package com.brunosong.deployment.system.datamigration.service.cache.onlinecampus.repositoy;

import com.brunosong.deployment.system.datamigration.service.cache.onlinecampus.entity.CurriculumCacheEntity;
import org.springframework.data.repository.CrudRepository;

public interface CurriculumRedisRepository extends CrudRepository<CurriculumCacheEntity,String> {
    CurriculumCacheEntity findByCurriculumId(String curriculumId);
}
