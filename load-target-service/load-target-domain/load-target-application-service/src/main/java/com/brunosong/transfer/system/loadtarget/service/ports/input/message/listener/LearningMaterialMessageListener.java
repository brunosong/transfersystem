package com.brunosong.transfer.system.loadtarget.service.ports.input.message.listener;


import com.brunosong.transfer.system.loadtarget.service.domain.entity.LearningMaterial;

public interface LearningMaterialMessageListener {
    void load(LearningMaterial learningMaterial);
}
