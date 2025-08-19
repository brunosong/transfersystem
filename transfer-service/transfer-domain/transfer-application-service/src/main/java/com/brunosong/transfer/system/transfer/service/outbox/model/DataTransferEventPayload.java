package com.brunosong.transfer.system.transfer.service.outbox.model;

import com.brunosong.transfer.system.domain.valueobject.DataMigrationId;
import com.brunosong.transfer.system.domain.valueobject.TransferId;
import com.brunosong.transfer.system.transfer.service.valueobject.TransType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
@Builder
@AllArgsConstructor
public class DataTransferEventPayload {

    @JsonProperty
    private TransferId transferId;

    @JsonProperty
    private byte[] data;

    @JsonProperty
    private DataMigrationId dataMigrationId;

    @JsonProperty
    private TransType transType;

    @JsonProperty
    private int chunkOffset;

    @JsonProperty
    private ZonedDateTime createdAt;

}
