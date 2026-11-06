package com.brunosong.deployment.system.targetagent.service.ports.output.message.publisher.deployment;

import com.brunosong.deployment.system.targetagent.service.dto.message.ApplyResultMessage;

public interface ApplyResultPublisher {

    void publish(ApplyResultMessage resultMessage);
}
