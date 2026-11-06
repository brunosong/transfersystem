package com.brunosong.deployment.system.targetagent.service.cache.target.entity;

import com.brunosong.deployment.system.targetagent.service.domain.valueobject.TargetSystemEnvironment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@RedisHash(value = "target", timeToLive = 3600)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class TargetCacheEntity implements Serializable {

    @Id
    private Long target_id;
    private String targetSystem;
    private TargetSystemEnvironment targetSystemEnvironment;

}
