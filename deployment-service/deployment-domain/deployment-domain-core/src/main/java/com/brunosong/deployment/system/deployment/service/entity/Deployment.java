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
    private SourceType sourceType;
    private DbType dbType;
    private TargetId targetId;
    private DeploymentStatus deploymentStatus;
    private TransType transType;
    private SourceConfigId sourceConfigId;
    private LocalDateTime createdAt;
    private String createUserId;
    private LocalDateTime updatedAt;
    private String resultMessage;
    private int totalChunkSize;

    private static final int SKELETON_NO = 0;

    /**
     * 청크마다의 상태(번호, WAITING/SENT/APPLIED/FAILED, 시도 번호). 내용은 담지 않는다.
     * 아직 연결 전이라 실제 앱에서는 늘 비어 있다. 접수 때 accept() 로 채우고,
     * 결과가 오면 recordSuccess()/recordFailure() 로 바꾸게 할 것이다.
     * 지금 결과 처리는 옛 표(deployment_chunks)를 따로 쓴다. 이 목록은 새 표(deployment_chunk)에 저장된다.
     */
    private final List<Chunk> chunks = new ArrayList<>();

    /** id 는 부모(BaseEntity)의 필드라 클래스에 붙인 @Builder 로는 받을 수 없어 생성자에 붙인다 */
    @Builder
    private Deployment(DeploymentId id, SourceId sourceId, SourceType sourceType, DbType dbType, TargetId targetId,
                       DeploymentStatus deploymentStatus, TransType transType, SourceConfigId sourceConfigId,
                       LocalDateTime createdAt, String createUserId, LocalDateTime updatedAt,
                       String resultMessage, int totalChunkSize, List<Chunk> chunks) {
        setId(id);
        this.sourceId = sourceId;
        this.sourceType = sourceType;
        this.dbType = dbType;
        this.targetId = targetId;
        this.deploymentStatus = deploymentStatus;
        this.transType = transType;
        this.sourceConfigId = sourceConfigId;
        this.createdAt = createdAt;
        this.createUserId = createUserId;
        this.updatedAt = updatedAt;
        this.resultMessage = resultMessage;
        this.totalChunkSize = totalChunkSize;
        if (chunks != null) {
            this.chunks.addAll(chunks);   // 저장소에서 되살릴 때만 넘긴다. 새 배포는 accept() 로 만든다
        }
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

    /**
     * 새 배포를 다 갖춘 상태로 한 번에 만든다. ID, 시작 상태(PENDING), 생성 시각은 여기서 정한다.
     * 새 배포는 이것으로만 만든다. 빌더는 저장소에서 되살릴 때 쓴다.
     * 청크 내용은 들고 있지 않는다. 내용은 아웃박스로 따로 나간다(도메인 모델: 청크 내용은 애그리거트에 넣지 않는다).
     * 청크는 뼈대 하나가 늘 있으므로 하나 이상이어야 한다.
     */
    public static Deployment create(SourceId sourceId, SourceConfigId sourceConfigId, TargetId targetId,
                                    TransType transType, SourceType sourceType, DbType dbType,
                                    int totalChunkSize) {
        if (totalChunkSize < 1) {
            throw new DeploymentDomainException("청크는 하나 이상이어야 한다: " + totalChunkSize);
        }
        return Deployment.builder()
                .id(new DeploymentId(UUID.randomUUID()))
                .sourceId(sourceId)
                .sourceConfigId(sourceConfigId)
                .targetId(targetId)
                .transType(transType)
                .sourceType(sourceType)
                .dbType(dbType)
                .deploymentStatus(DeploymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .totalChunkSize(totalChunkSize)
                .build();
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
        Chunk skeleton = Chunk.sent(SKELETON_NO, now);
        chunks.add(skeleton);
        for (int no = 1; no <= subjectCount; no++) {
            chunks.add(Chunk.waiting(no));
        }
        this.deploymentStatus = DeploymentStatus.IN_PROGRESS;
        return List.of(skeleton);
    }

    /**
     * 청크 하나가 반영됐다는 결과를 기록하고, 이제 보낼 청크 목록을 돌려준다.
     * 진행 중인 배포의 뼈대가 반영되면 기다리던 과목을 모두 보낸다(불변식 3).
     * 실패한 배포에 뼈대 성공이 늦게 오면 과목을 스스로 보내지 않는다. 운영자가 재전송해야 나간다.
     */
    public List<Chunk> recordSuccess(int chunkNo, Instant now) {
        chunk(chunkNo).recordSuccess();
        List<Chunk> toSend = new ArrayList<>();
        if (chunkNo == SKELETON_NO && deploymentStatus == DeploymentStatus.IN_PROGRESS) {
            for (Chunk c : chunks) {
                if (c.getStatus() == ChunkStatus.WAITING) {
                    c.send(now);
                    toSend.add(c);
                }
            }
        }
        updateStatus();
        return toSend;
    }

    /** 청크 하나가 실패했다는 결과를 기록한다. 어느 시도의 결과인지는 청크가 판단한다. */
    public void recordFailure(int chunkNo, int attempt, String message, Instant now) {
        chunk(chunkNo).recordFailure(attempt, message, now);
        updateStatus();
    }

    /**
     * 청크 상태로 배포 상태를 정한다.
     * 모두 APPLIED 면 SUCCEEDED(불변식 2), 응답을 기다리는 청크가 없는데 실패가 남아 있으면 FAILED 다.
     * 그 밖에는 지금 상태를 유지한다. 그래서 FAILED 가 스스로 IN_PROGRESS 로 돌아가지는 않는다.
     */
    private void updateStatus() {
        if (chunks.stream().allMatch(c -> c.getStatus() == ChunkStatus.APPLIED)) {
            this.deploymentStatus = DeploymentStatus.SUCCEEDED;
            return;
        }
        boolean awaiting = chunks.stream().anyMatch(c -> c.getStatus() == ChunkStatus.SENT);
        boolean failed = chunks.stream().anyMatch(c -> c.getStatus() == ChunkStatus.FAILED);
        if (!awaiting && failed) {
            this.deploymentStatus = DeploymentStatus.FAILED;
        }
    }

    /** 번호로 청크를 찾는다. 없는 번호의 결과는 잘못된 메시지라 예외를 던진다. */
    private Chunk chunk(int no) {
        return chunks.stream()
                .filter(c -> c.getNo() == no)
                .findFirst()
                .orElseThrow(() -> new DeploymentDomainException("Chunk " + no + " not found"));
    }

    /** 밖에서 청크를 넣거나 빼지 못하게 복사본을 준다. 직접 쓴 getter 라 @Getter 는 이것을 만들지 않는다 */
    public List<Chunk> getChunks() {
        return List.copyOf(chunks);
    }
}