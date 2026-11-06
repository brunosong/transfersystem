package com.brunosong.deployment.system.targetagent.service.dataaccess.target.adapter;

import com.brunosong.deployment.system.targetagent.service.dataaccess.target.mapper.TargetDataAccessMapper;
import com.brunosong.deployment.system.targetagent.service.dataaccess.target.repository.TargetJpaRepository;
import com.brunosong.deployment.system.targetagent.service.domain.entity.Target;
import com.brunosong.deployment.system.targetagent.service.ports.output.repository.TargetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TargetRepositoryImpl implements TargetRepository {

    private final TargetJpaRepository targetJpaRepository;
    private final TargetDataAccessMapper dataAccessMapper;

    @Override
    public Optional<Target> findById(Long targetId) {
        return targetJpaRepository.findById(targetId).map( entity ->
                dataAccessMapper.entityToDomain(entity)
        );
    }
}
