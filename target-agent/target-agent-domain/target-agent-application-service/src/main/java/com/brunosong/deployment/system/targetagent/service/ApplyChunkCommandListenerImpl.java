package com.brunosong.deployment.system.targetagent.service;

import com.brunosong.deployment.system.targetagent.service.domain.valueobject.AppliedContent;
import com.brunosong.deployment.system.targetagent.service.domain.valueobject.TargetSystemEnvironment;
import com.brunosong.deployment.system.targetagent.service.dto.message.ApplyChunkRequest;
import com.brunosong.deployment.system.targetagent.service.handler.DataPersistHandler;
import com.brunosong.deployment.system.targetagent.service.helper.MessageResponseHelper;
import com.brunosong.deployment.system.targetagent.service.ports.input.message.listener.ApplyChunkCommandListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplyChunkCommandListenerImpl implements ApplyChunkCommandListener {

    private final DataPersistHandler dataPersistHandler;
    private final MessageResponseHelper messageResponseHelper;

    @Override
    public void apply(ApplyChunkRequest request) {

        // 계약 규칙 1: 자기 대상이 아닌 명령에는 결과를 보내지 않는다
        if (!dataPersistHandler.supports(request.targetSystem())) {
            log.warn("맡지 않은 대상의 명령을 건너뜁니다. targetSystem={}, deploymentId={}, chunkNo={}",
                    request.targetSystem(), request.deploymentId(), request.chunkNo());
            return;
        }

        AppliedContent applied;
        try {
            applied = dataPersistHandler.persist(
                    request.targetSystem(),
                    TargetSystemEnvironment.valueOf(request.environment()),
                    request.curriculumId(),
                    request.content());
        } catch (Exception e) {
            messageResponseHelper.failsResponseMessage(request, e);
            pause();
            return;
        }

        // 발행을 try 밖에 둔다. 안에 두면 성공 발행의 오류가 실패 결과로 또 나간다
        messageResponseHelper.successResponseMessage(request, applied);
        pause();
    }

    private void pause() {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
