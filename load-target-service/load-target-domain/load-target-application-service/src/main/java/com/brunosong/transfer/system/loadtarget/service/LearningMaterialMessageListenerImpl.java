package com.brunosong.transfer.system.loadtarget.service;

import com.brunosong.transfer.system.loadtarget.service.domain.entity.LearningMaterial;
import com.brunosong.transfer.system.loadtarget.service.ports.input.message.listener.LearningMaterialMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LearningMaterialMessageListenerImpl implements LearningMaterialMessageListener {

    // 여기에서 디비를 선택할 수 있는 클래스를 적용한다.

    @Override
    public void load(LearningMaterial learningMaterial) {

    }

}
