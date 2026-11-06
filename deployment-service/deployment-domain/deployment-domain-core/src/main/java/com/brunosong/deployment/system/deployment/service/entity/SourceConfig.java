package com.brunosong.deployment.system.deployment.service.entity;

import com.brunosong.deployment.system.domain.entity.BaseEntity;
import com.brunosong.deployment.system.deployment.service.valueobject.DbType;
import com.brunosong.deployment.system.deployment.service.valueobject.SourceConfigId;
import com.brunosong.deployment.system.deployment.service.valueobject.SourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
// 핵심 도메인이 아니라 롬복 사용
public class SourceConfig extends BaseEntity<SourceConfigId> {
    private SourceType sourceType;
    private DbType dbType;
    private String host;
    private int port;
    private String database;
    private String username;
    private String password;
    private String tableOrCollection;
}
