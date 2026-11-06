package com.brunosong.deployment.system.targetagent.service.handler;

import com.brunosong.deployment.system.targetagent.service.domain.entity.Target;
import com.brunosong.deployment.system.targetagent.service.domain.exception.TargetNotFoundException;
import com.brunosong.deployment.system.targetagent.service.ports.output.cache.TargetCachePort;
import com.brunosong.deployment.system.targetagent.service.ports.output.repository.TargetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 대상 정보를 처리하는 핸들러 클래스입니다.
 * 이 클래스는 대상 정보를 캐시에서 찾고, 없을 경우 데이터베이스에서 조회하여 캐시에 저장합니다.
 * 또한, 대상 정보가 없을 경우 예외를 발생시킵니다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TargetHandler {

    private final TargetRepository targetRepository;
    private final TargetCachePort targetCachePort;

    public Target findTarget(Long migrationInfoId) {

        Optional<Target> result = targetCachePort.findById(migrationInfoId);

        if (result.isEmpty()) {
            result = targetRepository.findById(migrationInfoId);

            if (result.isPresent()) {
                log.info("Target found in repository for ID: {}", migrationInfoId);
                Target target = result.get();

                targetCachePort.save(target);
                log.info("Target saved to cache for ID: {}", migrationInfoId);

                return target;
            } else {
                log.error("Target not found for ID: {}", migrationInfoId);
                throw new TargetNotFoundException("Target not found for ID: " + migrationInfoId);
            }
        } else {
            log.info("Target found in cache for ID: {}", migrationInfoId);
            return result.get();
        }
    }

}
