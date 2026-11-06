package com.brunosong.deployment.system.targetagent.service.cache.target.adapter;

import com.brunosong.deployment.system.targetagent.service.domain.entity.Target;
import com.brunosong.deployment.system.targetagent.service.ports.output.cache.TargetCachePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TargetCacheAdapter implements TargetCachePort {

    @Override
    public void save(Target target) {
        // 캐쉬 TTL은 10분으로 준다.
    }

    @Override
    public Optional<Target> findById(Long migrationInfoId) {
        return Optional.empty();
    }
}
