package com.brunosong.transfer.system.datamigration.service.cache.onlinecampus.repositoy;

import com.brunosong.transfer.system.datamigration.service.cache.onlinecampus.entity.CurriculumCacheEntity;
import org.springframework.data.repository.CrudRepository;

public interface CurriculumRedisRepository extends CrudRepository<CurriculumCacheEntity,String> {
    CurriculumCacheEntity findByCurriculumId(String curriculumId);
}
