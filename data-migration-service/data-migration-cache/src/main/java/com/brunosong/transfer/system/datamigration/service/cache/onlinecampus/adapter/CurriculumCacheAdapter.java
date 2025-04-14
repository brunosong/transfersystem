package com.brunosong.transfer.system.datamigration.service.cache.onlinecampus.adapter;

import com.brunosong.transfer.system.datamigration.service.cache.onlinecampus.entity.CurriculumCacheEntity;
import com.brunosong.transfer.system.datamigration.service.cache.onlinecampus.repositoy.CurriculumRedisRepository;
import com.brunosong.transfer.system.datamigration.service.ports.output.cache.CurriculumCachePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurriculumCacheAdapter implements CurriculumCachePort {
    private final CurriculumRedisRepository cacheRepository;

    public void save(long id, String curriculumId) {

        CurriculumCacheEntity curriculumCacheEntity = CurriculumCacheEntity.builder()
                .id(id)
                .curriculumId(curriculumId)
                .build();
        cacheRepository.save(curriculumCacheEntity);
    }

    public long findById(String curriculumId) {
        return cacheRepository.findById(curriculumId).orElseThrow().getId();
    }
}
