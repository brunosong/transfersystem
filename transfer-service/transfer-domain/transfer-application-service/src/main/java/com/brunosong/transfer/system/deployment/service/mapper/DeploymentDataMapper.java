package com.brunosong.transfer.system.deployment.service.mapper;

import com.brunosong.transfer.system.deployment.service.helper.deployment.TargetCatalog;
import com.brunosong.transfer.system.deployment.service.valueobject.ChunkContent;
import com.brunosong.transfer.system.domain.valueobject.DataMigrationId;
import com.brunosong.transfer.system.deployment.service.dto.create.DataMigrationResponse;
import com.brunosong.transfer.system.deployment.service.dto.create.DeploymentRequest;
import com.brunosong.transfer.system.deployment.service.entity.Deployment;
import com.brunosong.transfer.system.deployment.service.event.DeploymentRequestEvent;
import com.brunosong.transfer.system.deployment.service.outbox.model.DeploymentEventPayload;
import com.brunosong.transfer.system.deployment.service.valueobject.SourceConfigId;
import com.brunosong.transfer.system.deployment.service.valueobject.SourceContentData;
import com.brunosong.transfer.system.deployment.service.valueobject.SourceId;
import org.springframework.stereotype.Component;

@Component
public class DeploymentDataMapper {

    public Deployment deploymentRequestToDeployment(DeploymentRequest request) {
        return Deployment.builder()
                .sourceId(new SourceId(request.getSourceId()))
                .sourceConfigId(new SourceConfigId(request.getSourceConfigId()))
                .dataMigrationInfoId(new DataMigrationId(request.getDataMigrationInfoId()))
                .transType(request.getTransType())
                .build();
    }

    public SourceConfigId toSourceConfigId(DeploymentRequest request) {
        return new SourceConfigId(request.getSourceConfigId());
    }

    public Deployment dataMigrationResponseToDeployment(DataMigrationResponse dataMigrationResponse) {
        return Deployment.builder()
                .id(dataMigrationResponse.getDeploymentId())
                .deploymentStatus(dataMigrationResponse.getDeploymentStatus())
                .resultMessage(dataMigrationResponse.getMessage())
                .sourceContentData(SourceContentData.builder().chunkOffset(dataMigrationResponse.getChunkOffset()).build())
                .build();
    }

    public DeploymentEventPayload deploymentRequestEventToDeploymentEventPayload(DeploymentRequestEvent deploymentRequestEvent) {
        TargetCatalog.Target target = TargetCatalog.find(deploymentRequestEvent.getDeployment().getDataMigrationId().getValue());
        ChunkContent content = deploymentRequestEvent.getContent();
        return DeploymentEventPayload.builder()
                .deploymentId(deploymentRequestEvent.getDeployment().getId().getValue())
                .targetSystem(target.system())
                .environment(target.environment())
                .transType(deploymentRequestEvent.getDeployment().getTransType())
                .curriculumId(deploymentRequestEvent.getDeployment().getSourceContentData().getCurriculumId())
                .skeleton(content instanceof ChunkContent.Skeleton skeleton ? skeleton : null)
                .subjectChunk(content instanceof ChunkContent.SubjectChunk chunk ? chunk : null)
                .chunkOffset(deploymentRequestEvent.getChunkOffset())
                .createdAt(deploymentRequestEvent.getCreatedAt())
                .build();
    }
}
