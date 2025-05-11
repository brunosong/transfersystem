package com.brunosong.transfer.system.datamigration.service.cache.datamigrationinfo.entity;

import com.brunosong.transfer.system.datamigration.service.domain.valueobject.TargetSystemEnvironment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@RedisHash(value = "dataMigration", timeToLive = 3600)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class DataMigrationInfoCacheEntity implements Serializable {

    @Id
    private Long datamigration_id;
    private String targetSystem;
    private TargetSystemEnvironment targetSystemEnvironment;

}
