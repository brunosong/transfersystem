package com.brunosong.transfersystem.main.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ServiceSelector {

    private TranActionService tranActionAiService;
    private TranActionService tranActionAiKafkaService;

    private final ThreadLocal<TranActionService> currentService = ThreadLocal.withInitial(() -> tranActionAiService);

    @Autowired
    public ServiceSelector(@Qualifier("tranActionAiKafkaService") TranActionService tranActionAiKafkaService,
                           @Qualifier("tranActionAiService") TranActionService tranActionAiService) {

        this.tranActionAiService = tranActionAiService;
        this.tranActionAiKafkaService = tranActionAiKafkaService;
    }

    public void useServiceA() {
        currentService.set(tranActionAiService);
    }

    public void useServiceB() {
        currentService.set(tranActionAiKafkaService);
    }

    public TranActionService getCurrentService() {
        return currentService.get();
    }
}
