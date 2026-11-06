package com.brunosong.deployment.system.deployment.service.dataaccess.source.adapter;

import com.brunosong.deployment.system.deployment.service.dataaccess.source.mapper.SourceDataAccessMapper;
import com.brunosong.deployment.system.deployment.service.dataaccess.source.repository.SourceConfigJpaRepository;
import com.brunosong.deployment.system.deployment.service.entity.SourceConfig;
import com.brunosong.deployment.system.deployment.service.ports.output.repository.SourceConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SourceConfigRepositoryImpl implements SourceConfigRepository {

    private final SourceConfigJpaRepository sourceConfigJpaRepository;
    private final SourceDataAccessMapper dataAccessMapper;
    @Override
    public Optional<SourceConfig> findSourceConfigInfo(Long sourceConfigId) {
        return sourceConfigJpaRepository.findById(sourceConfigId).map(dataAccessMapper::entityToSourceConfig);
    }
}
