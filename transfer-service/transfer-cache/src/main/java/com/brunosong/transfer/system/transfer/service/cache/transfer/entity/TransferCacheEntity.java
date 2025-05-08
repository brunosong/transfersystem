package com.brunosong.transfer.system.transfer.service.cache.transfer.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@RedisHash(value = "transfer", timeToLive = 3600)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class TransferCacheEntity implements Serializable {
    @Id
    private String transferId;
    private int totalChunkSize;
}
