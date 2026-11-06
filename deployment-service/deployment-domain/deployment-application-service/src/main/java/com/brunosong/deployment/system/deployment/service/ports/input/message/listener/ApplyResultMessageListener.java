package com.brunosong.deployment.system.deployment.service.ports.input.message.listener;

import com.brunosong.deployment.system.deployment.service.dto.create.ApplyResultMessage;

public interface ApplyResultMessageListener {
    void deploymentStatusUpdate(ApplyResultMessage applyResultMessage);
}
