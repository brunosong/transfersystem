package com.brunosong.deployment.system.deployment.service.cache.deployment.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@RedisHash(value = "deployment", timeToLive = 3600)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class DeploymentCacheEntity implements Serializable {
    @Id
    private String deploymentId;
    private int totalChunkSize;
}
