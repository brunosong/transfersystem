package com.brunosong.deployment.system.targetagent.service.ports.input.message.listener;

import com.brunosong.deployment.system.targetagent.service.dto.message.ApplyChunkRequest;

public interface ApplyChunkCommandListener {

    void apply(ApplyChunkRequest request);
}
