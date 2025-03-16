package com.brunosong.transfer.system.transfer.service.entity;

import com.brunosong.transfer.system.domain.entity.BaseEntity;
import com.brunosong.transfer.system.transfer.service.valueobject.DbType;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceConfigId;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceType;
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
