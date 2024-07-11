package com.brunosong.transfersystem.main.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ServiceSelector2 {

    private TranActionService tranActionAiService;

    private final ThreadLocal<TranActionService> currentService = ThreadLocal.withInitial(() -> tranActionAiService);

    @Autowired
    public ServiceSelector2(TranActionService tranActionAiService) {
        this.tranActionAiService = tranActionAiService;
    }

    public void useServiceA(TranActionService tranActionAiService) {
        currentService.set(tranActionAiService);
    }

    public TranActionService getCurrentService() {
        return currentService.get();
    }
}
