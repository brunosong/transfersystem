package com.brunosong.deployment.system.deployment.service.event;

import com.brunosong.deployment.system.deployment.service.valueobject.ChunkContent;
import com.brunosong.deployment.system.deployment.service.entity.Deployment;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
public class DeploymentRequestEvent extends DeploymentEvent {

    private final int chunkOffset;
    /** 대상에 보낼 커리큘럼 ID. 배포는 청크 내용을 들고 있지 않아서 청크와 함께 이벤트에 싣는다 */
    private final String curriculumId;
    private final ChunkContent content;

    public DeploymentRequestEvent(Deployment deployment, ZonedDateTime createdAt, int chunkOffset,
                                  String curriculumId, ChunkContent content) {
        super(deployment, createdAt);
        this.chunkOffset = chunkOffset;
        this.curriculumId = curriculumId;
        this.content = content;
    }

}
