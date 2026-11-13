package com.brunosong.deployment.system.deployment.service.outbox.model;

import com.brunosong.deployment.system.deployment.service.valueobject.ChunkContent;
import com.brunosong.deployment.system.deployment.service.valueobject.TransType;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
 * 내용은 둘 중 하나만 채운다. ChunkContent 에 Jackson 다형성 표시를 달면 도메인 모듈이 Jackson 을 알게 된다.
 * 아웃박스가 참조만 담게 되면(ADR 0004) 이 칸들은 없어진다.
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
    private ChunkContent.Skeleton skeleton;

    @JsonProperty
    private ChunkContent.SubjectChunk subjectChunk;

    @JsonProperty
    private int chunkOffset;

    @JsonProperty
    private ZonedDateTime createdAt;

    @JsonIgnore
    public ChunkContent getContent() {
        return skeleton != null ? skeleton : subjectChunk;
    }

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
