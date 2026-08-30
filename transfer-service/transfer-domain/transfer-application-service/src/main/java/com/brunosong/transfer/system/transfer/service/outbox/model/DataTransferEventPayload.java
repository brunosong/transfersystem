package com.brunosong.transfer.system.transfer.service.outbox.model;

import com.brunosong.transfer.system.transfer.service.valueobject.TransType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * 아웃박스에 적히는 페이로드. 스케줄러가 다시 읽어 발행한다.
 *
 * 식별자를 TransferId, DataMigrationId 대신 원시 타입으로 담는다. 값객체는 기본 생성자가
 * 없어 역직렬화가 되지 않고, 그렇다고 common-domain 에 Jackson 을 넣으면 의존성이 하나도
 * 없는 도메인 모듈이 깨진다. 페이로드는 도메인 모델이 아니라 저장되는 계약이므로 이쪽이 맞다.
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DataTransferEventPayload {

    @JsonProperty
    private UUID transferId;

    @JsonProperty
    private Long dataMigrationId;

    @JsonProperty
    private TransType transType;

    @JsonProperty
    private String sourceId;

    @JsonProperty
    private byte[] chunkData;

    @JsonProperty
    private int chunkOffset;

    @JsonProperty
    private ZonedDateTime createdAt;

}
