package com.brunosong.deployment.system.deployment.service.dto.create;

import com.brunosong.deployment.system.domain.valueobject.DeploymentId;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplyResultMessage {
    private DeploymentId deploymentId;
    /** 대상이 반영했으면 true(Applied), 거절했으면 false(Rejected). 배포 상태는 이것을 보고 도메인이 정한다 */
    private boolean applied;
    private int chunkOffset;
    /** 몇 번째 시도의 결과인가. 실패 결과가 지금 시도의 것인지 가릴 때 쓴다 */
    private int attempt;
    /** 거절 사유. 반영됐으면 비어 있다 */
    private String message;
}
