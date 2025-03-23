package com.brunosong.transfer.system.datamigration.service.dto.message;

import com.brunosong.transfer.system.datamigration.service.domain.valueobject.SourceContentData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DataMigrationRequest {
    private String transferId;
    private Long dataMigrationId;
    private SourceContentData sourceContentData;
}
