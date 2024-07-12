package com.brunosong.transfersystem.main.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/* 개발자가 어떤인지하기 편하도록 ApplicationContext를 사용하여 Bean을 주입하는 방식보다 이 방법을 선택 */
@Component
public class TranActionServiceSelector {

    private TranActionService tranActionDefaultService;
    private TranActionService tranActionAiService;
    private TranActionService tranActionAiKafkaService;

    /* 동시성 이슈를 해결하기 위해 ThreadLocal 사용 */
    private final ThreadLocal<TranActionService> currentService = ThreadLocal.withInitial(() -> tranActionDefaultService);

    @Autowired
    public TranActionServiceSelector(@Qualifier("tranActionDefaultService") TranActionService tranActionDefaultService,
                                     @Qualifier("tranActionAiKafkaService") TranActionService tranActionAiKafkaService,
                                     @Qualifier("tranActionAiService") TranActionService tranActionAiService) {

        this.tranActionDefaultService = tranActionDefaultService;
        this.tranActionAiService = tranActionAiService;
        this.tranActionAiKafkaService = tranActionAiKafkaService;
    }


    public void useDefaultService() {
        currentService.set(tranActionDefaultService);
    }

    public void useAiService() {
        currentService.set(tranActionAiService);
    }

    public void useAiKafkaService() {
        currentService.set(tranActionAiKafkaService);
    }

    public TranActionService getCurrentService() {
        return currentService.get();
    }

    public void clearCurrentService() {
        currentService.remove();
    }

}
