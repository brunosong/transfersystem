package com.brunosong.transfer.system.transfer.service.outbox.model;

import com.brunosong.transfer.system.domain.valueobject.DataMigrationId;
import com.brunosong.transfer.system.domain.valueobject.TransferStatus;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceConfigId;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceContentData;
import com.brunosong.transfer.system.transfer.service.valueobject.SourceId;
import com.brunosong.transfer.system.transfer.service.valueobject.TransType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DataTransferEventPayload {

    @JsonProperty
    private SourceId sourceId;

    @JsonProperty
    private SourceContentData sourceContentData;

    @JsonProperty
    private DataMigrationId dataMigrationId;

    @JsonProperty
    private TransferStatus transferStatus;

    @JsonProperty
    private TransType transType;

    @JsonProperty
    private SourceConfigId sourceConfigId;

    @JsonProperty
    private int totalChunkSize;

}
