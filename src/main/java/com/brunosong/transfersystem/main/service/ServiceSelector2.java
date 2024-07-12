package com.brunosong.transfersystem.main.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/* 변경은 한 클래스에서만 이루어 질수 있도록 클래스를 생성해준다. */
@Component
public class ServiceSelector2 {

    private MigrationService tranActionAiService;

    private final ThreadLocal<MigrationService> currentService = ThreadLocal.withInitial(() -> tranActionAiService);

    @Autowired
    public ServiceSelector2(MigrationService tranActionAiService) {
        this.tranActionAiService = tranActionAiService;
    }

    public void useServiceA(MigrationService tranActionAiService) {
        currentService.set(tranActionAiService);
    }

    public MigrationService getCurrentService() {
        return currentService.get();
    }
}
