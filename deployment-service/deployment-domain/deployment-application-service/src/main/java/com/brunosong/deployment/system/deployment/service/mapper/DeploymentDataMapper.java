package com.brunosong.deployment.system.deployment.service.mapper;

import com.brunosong.deployment.system.deployment.service.helper.deployment.TargetCatalog;
import com.brunosong.deployment.system.deployment.service.valueobject.ChunkContent;
import com.brunosong.deployment.system.domain.valueobject.TargetId;
import com.brunosong.deployment.system.deployment.service.dto.create.ApplyResultMessage;
import com.brunosong.deployment.system.deployment.service.dto.create.CreateDeploymentResponse;
import com.brunosong.deployment.system.deployment.service.dto.create.DeploymentRequest;
import com.brunosong.deployment.system.deployment.service.entity.Deployment;
import com.brunosong.deployment.system.deployment.service.entity.SourceConfig;
import com.brunosong.deployment.system.deployment.service.outbox.model.DeploymentEventPayload;
import com.brunosong.deployment.system.deployment.service.valueobject.SourceConfigId;
import com.brunosong.deployment.system.deployment.service.valueobject.SourceId;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
public class DeploymentDataMapper {

    /** 요청과 원본 설정, 청크 수로 새 배포를 만든다. 값이 다 모인 뒤에 한 번에 만든다 */
    public Deployment toDeployment(DeploymentRequest request, SourceConfig sourceConfig,
                                   String curriculumId, int totalChunkSize) {
        return Deployment.create(
                toSourceId(request),
                toSourceConfigId(request),
                curriculumId,
                new TargetId(request.targetId()),
                request.transType(),
                sourceConfig.getSourceType(),
                sourceConfig.getDbType(),
                totalChunkSize);
    }

    public SourceId toSourceId(DeploymentRequest request) {
        return new SourceId(request.sourceId());
    }

    /** 접수한 배포를 응답으로 바꾼다. 도메인 객체가 컨트롤러로 나가지 않게 여기서 끊는다 */
    public CreateDeploymentResponse deploymentToCreateDeploymentResponse(Deployment deployment) {
        return new CreateDeploymentResponse(deployment.getId().getValue(), deployment.getTotalChunkSize());
    }

    public SourceConfigId toSourceConfigId(DeploymentRequest request) {
        return new SourceConfigId(request.sourceConfigId());
    }

    public Deployment applyResultMessageToDeployment(ApplyResultMessage applyResultMessage) {
        return Deployment.builder()
                .id(applyResultMessage.getDeploymentId())
                .deploymentStatus(applyResultMessage.getDeploymentStatus())
                .resultMessage(applyResultMessage.getMessage())
                .build();
    }

    /** 청크 하나를 아웃박스에 적을 페이로드로 바꾼다 */
    public DeploymentEventPayload toDeploymentEventPayload(Deployment deployment, int chunkOffset,
                                                           ChunkContent content, ZonedDateTime createdAt) {
        TargetCatalog.Target target = TargetCatalog.find(deployment.getTargetId().getValue());
            return DeploymentEventPayload.builder()
                    .deploymentId(deployment.getId().getValue())
                    .targetSystem(target.system())
                    .environment(target.environment())
                    .transType(deployment.getTransType())
                    .curriculumId(deployment.getCurriculumId())
                    .skeleton(content instanceof ChunkContent.Skeleton skeleton ? skeleton : null)
                    .subjectChunk(content instanceof ChunkContent.SubjectChunk chunk ? chunk : null)
                    .chunkOffset(chunkOffset)
                    .createdAt(createdAt)
                    .build();
    }
}
