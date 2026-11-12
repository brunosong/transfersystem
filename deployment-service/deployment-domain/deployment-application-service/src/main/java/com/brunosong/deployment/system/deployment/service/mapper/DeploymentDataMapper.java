package com.brunosong.deployment.system.deployment.service.mapper;

import com.brunosong.deployment.system.deployment.service.helper.deployment.TargetCatalog;
import com.brunosong.deployment.system.deployment.service.valueobject.ChunkContent;
import com.brunosong.deployment.system.domain.valueobject.TargetId;
import com.brunosong.deployment.system.deployment.service.dto.create.ApplyResultMessage;
import com.brunosong.deployment.system.deployment.service.dto.create.CreateDeploymentResponse;
import com.brunosong.deployment.system.deployment.service.dto.create.DeploymentRequest;
import com.brunosong.deployment.system.deployment.service.entity.Deployment;
import com.brunosong.deployment.system.deployment.service.event.DeploymentRequestEvent;
import com.brunosong.deployment.system.deployment.service.outbox.model.DeploymentEventPayload;
import com.brunosong.deployment.system.deployment.service.valueobject.SourceConfigId;
import com.brunosong.deployment.system.deployment.service.valueobject.SourceId;
import org.springframework.stereotype.Component;

@Component
public class DeploymentDataMapper {

    public Deployment deploymentRequestToDeployment(DeploymentRequest request) {
        return Deployment.builder()
                .sourceId(new SourceId(request.getSourceId()))
                .sourceConfigId(new SourceConfigId(request.getSourceConfigId()))
                .targetId(new TargetId(request.getTargetId()))
                .transType(request.getTransType())
                .build();
    }

    /** 접수한 배포를 응답으로 바꾼다. 도메인 객체가 컨트롤러로 나가지 않게 여기서 끊는다 */
    public CreateDeploymentResponse deploymentToCreateDeploymentResponse(Deployment deployment) {
        return new CreateDeploymentResponse(deployment.getId().getValue(), deployment.getTotalChunkSize());
    }

    public SourceConfigId toSourceConfigId(DeploymentRequest request) {
        return new SourceConfigId(request.getSourceConfigId());
    }

    public Deployment applyResultMessageToDeployment(ApplyResultMessage applyResultMessage) {
        return Deployment.builder()
                .id(applyResultMessage.getDeploymentId())
                .deploymentStatus(applyResultMessage.getDeploymentStatus())
                .resultMessage(applyResultMessage.getMessage())
                .build();
    }

    public DeploymentEventPayload deploymentRequestEventToDeploymentEventPayload(DeploymentRequestEvent deploymentRequestEvent) {
        TargetCatalog.Target target = TargetCatalog.find(deploymentRequestEvent.getDeployment().getTargetId().getValue());
        ChunkContent content = deploymentRequestEvent.getContent();
        return DeploymentEventPayload.builder()
                .deploymentId(deploymentRequestEvent.getDeployment().getId().getValue())
                .targetSystem(target.system())
                .environment(target.environment())
                .transType(deploymentRequestEvent.getDeployment().getTransType())
                .curriculumId(deploymentRequestEvent.getCurriculumId())
                .skeleton(content instanceof ChunkContent.Skeleton skeleton ? skeleton : null)
                .subjectChunk(content instanceof ChunkContent.SubjectChunk chunk ? chunk : null)
                .chunkOffset(deploymentRequestEvent.getChunkOffset())
                .createdAt(deploymentRequestEvent.getCreatedAt())
                .build();
    }
}
