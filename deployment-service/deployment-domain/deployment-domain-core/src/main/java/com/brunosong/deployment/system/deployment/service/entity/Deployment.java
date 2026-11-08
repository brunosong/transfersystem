package com.brunosong.deployment.system.deployment.service.entity;

import com.brunosong.deployment.system.domain.entity.AggregateRoot;
import com.brunosong.deployment.system.domain.valueobject.TargetId;
import com.brunosong.deployment.system.domain.valueobject.DeploymentId;
import com.brunosong.deployment.system.domain.valueobject.DeploymentStatus;
import com.brunosong.deployment.system.deployment.service.exception.DeploymentDomainException;
import com.brunosong.deployment.system.deployment.service.valueobject.*;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Deployment extends AggregateRoot<DeploymentId> {

    private SourceId sourceId;
    private SourceContentData sourceContentData;
    private TargetId targetId;
    private DeploymentStatus deploymentStatus;
    private TransType transType;
    private SourceConfigId sourceConfigId;
    private LocalDateTime createdAt;
    private String createUserId;
    private LocalDateTime updatedAt;
    private String resultMessage;
    private int totalChunkSize;
    private final List<Chunk> chunks = new ArrayList<>();

    /** id 는 부모(BaseEntity)의 필드라 클래스에 붙인 @Builder 로는 받을 수 없어 생성자에 붙인다 */
    @Builder
    private Deployment(DeploymentId id, SourceId sourceId, SourceContentData sourceContentData, TargetId targetId,
                       DeploymentStatus deploymentStatus, TransType transType, SourceConfigId sourceConfigId,
                       LocalDateTime createdAt, String createUserId, LocalDateTime updatedAt,
                       String resultMessage, int totalChunkSize) {
        setId(id);
        this.sourceId = sourceId;
        this.sourceContentData = sourceContentData;
        this.targetId = targetId;
        this.deploymentStatus = deploymentStatus;
        this.transType = transType;
        this.sourceConfigId = sourceConfigId;
        this.createdAt = createdAt;
        this.createUserId = createUserId;
        this.updatedAt = updatedAt;
        this.resultMessage = resultMessage;
        this.totalChunkSize = totalChunkSize;
    }

    /** 상태를 그대로 덮어쓴다. 규칙을 거치지 않으므로 연결 단계에서 지운다. */
    @Deprecated(forRemoval = true)
    public void updateDeploymentStatus(DeploymentStatus deploymentStatus) {
        this.deploymentStatus = deploymentStatus;
    }

    /** 옛 상태 기계(PENDING → SENT). 새 상태 기계로 바꾸면서 연결 단계에서 지운다. */
    @Deprecated(forRemoval = true)
    public void markSent() {
        if (this.deploymentStatus != DeploymentStatus.PENDING) {
            throw new IllegalStateException("Cannot mark sent from " + this.deploymentStatus);
        }
        this.deploymentStatus = DeploymentStatus.SENT;
        this.updatedAt = LocalDateTime.now();
    }

    /** 옛 상태 기계(SENT → PROCESSED). 연결 단계에서 지운다. */
    @Deprecated(forRemoval = true)
    public void markProcessed() {
        if (this.deploymentStatus != DeploymentStatus.SENT) {
            throw new IllegalStateException("Cannot mark processed from " + this.deploymentStatus);
        }
        this.deploymentStatus = DeploymentStatus.PROCESSED;
        this.updatedAt = LocalDateTime.now();
    }

    /** 옛 상태 기계(PROCESSED → SUCCESS). 연결 단계에서 지운다. */
    @Deprecated(forRemoval = true)
    public void markSuccess() {
        if (this.deploymentStatus != DeploymentStatus.PROCESSED) {
            throw new IllegalStateException("Cannot mark success from " + this.deploymentStatus);
        }
        this.deploymentStatus = DeploymentStatus.SUCCESS;
        this.updatedAt = LocalDateTime.now();
    }

    /** 옛 상태 기계의 실패. 연결 단계에서 지운다. */
    @Deprecated(forRemoval = true)
    public void markFailed() {
        this.deploymentStatus = DeploymentStatus.FAILED;
        this.updatedAt = LocalDateTime.now();
    }

    /** 새 배포에 ID, 시작 상태, 생성 시각, 읽어 온 원본 내용을 채운다. */
    public void initializeDeployment(SourceContentData sourceContentData) {
        setId(new DeploymentId(UUID.randomUUID()));
        this.deploymentStatus = DeploymentStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.sourceContentData = sourceContentData;
    }

    /** 아직 아무것도 채워지지 않은 새 배포인지 확인한다. */
    public void validateInitialDeployment() {
        if (deploymentStatus != null || getCreatedAt() != null || getSourceContentData() != null) {
            throw new DeploymentDomainException("Deployment is not in correct state for initialization!");
        }
    }

    /**
     * 배포를 접수한다. 뼈대 청크 하나(0번)는 바로 보내고(SENT), 과목 청크(1번부터)는 기다리게(WAITING) 만든다.
     * 대상은 학기가 있어야 과목을 저장할 수 있어서 뼈대를 먼저 보낸다(불변식 3).
     * 지금 보낼 청크 목록을 돌려준다. 아웃박스 행은 애플리케이션 서비스가 이 목록으로 만든다.
     * 이미 접수했거나 과목 수가 음수면 예외를 던진다. 청크 구성은 접수 때 정해지고 바뀌지 않는다(불변식 1).
     */
    public List<Chunk> accept(int subjectCount, Instant now) {
        if (!chunks.isEmpty()) {
            throw new DeploymentDomainException("Deployment is already accepted");
        }
        if (subjectCount < 0) {
            throw new DeploymentDomainException("Subject count cannot be negative: " + subjectCount);
        }
        Chunk skeleton = Chunk.sent(0, now);
        chunks.add(skeleton);
        for (int no = 1; no <= subjectCount; no++) {
            chunks.add(Chunk.waiting(no));
        }
        this.deploymentStatus = DeploymentStatus.IN_PROGRESS;
        return List.of(skeleton);
    }

    /** 밖에서 청크를 넣거나 빼지 못하게 복사본을 준다. 직접 쓴 getter 라 @Getter 는 이것을 만들지 않는다 */
    public List<Chunk> getChunks() {
        return List.copyOf(chunks);
    }
}