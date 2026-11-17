package com.brunosong.deployment.system.deployment.service.outbox.model;

import com.brunosong.deployment.system.deployment.service.valueobject.TransType;
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
 * 식별자를 DeploymentId 대신 원시 타입으로 담는다. 값객체는 기본 생성자가 없어 역직렬화가 되지 않고,
 * 그렇다고 common-domain 에 Jackson 을 넣으면 의존성이 하나도 없는 도메인 모듈이 깨진다.
 * 페이로드는 도메인 모델이 아니라 저장되는 계약이므로 이쪽이 맞다.
 *
 * 청크 내용은 담지 않는다. 배포 ID와 청크 번호로 가리키기만 하고, 발행할 때 내용 표(deployment_chunk_payload)에서 읽는다(ADR 0004).
 * 대상 이름, 환경, 커리큘럼 ID는 작고 접수한 뒤 바뀌지 않아 함께 둔다. 발행할 때 배포를 다시 읽지 않아도 된다.
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeploymentEventPayload {

    @JsonProperty
    private UUID deploymentId;

    @JsonProperty
    private String targetSystem;

    @JsonProperty
    private String environment;

    @JsonProperty
    private TransType transType;

    @JsonProperty
    private String curriculumId;

    @JsonProperty
    private int chunkOffset;

    /** 몇 번째 시도인지. 첫 전송은 1, 재전송하면 늘어난다 */
    @JsonProperty
    private int attempt;

    @JsonProperty
    private ZonedDateTime createdAt;

    /**
     * Kafka 메시지 키. "대상 시스템:환경:커리큘럼 ID" (도메인 모델 7절).
     * 대상에서 서로 덮어쓰면 안 되는 단위가 "커리큘럼 × 대상"이라 이것으로 순서를 지킨다.
     * 같은 배포의 청크도, 같은 커리큘럼의 옛 배포와 새 배포도 같은 파티션에 들어간다.
     * 원본 문서 ID(sourceId)가 아니라 커리큘럼 ID를 쓴다. 고쳐 다시 올린 원본은 문서 ID가 바뀔 수 있다.
     * 숫자 대상 ID 대신 이름을 쓴다. 숫자는 임시 목록(TargetCatalog)의 순서라 바뀔 수 있다.
     */
    public String messageKey() {
        return targetSystem + ":" + environment + ":" + curriculumId;
    }
}
