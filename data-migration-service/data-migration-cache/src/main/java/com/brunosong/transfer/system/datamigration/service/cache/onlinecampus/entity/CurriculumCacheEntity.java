package com.brunosong.transfer.system.datamigration.service.cache.onlinecampus.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@RedisHash(value = "curriculum", timeToLive = 3600)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CurriculumCacheEntity implements Serializable {

    @Id
    private String curriculumId;

    private long id;

}
