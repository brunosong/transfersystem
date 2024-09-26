package com.brunosong.transfer.system.transfer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/* 개발자가 어떤인지하기 편하도록 ApplicationContext를 사용하여 Bean을 주입하는 방식보다 이 방법을 선택 */
@Component
public class MigrationServiceSelector {

    private MigrationService migrationDefaultService;
    private MigrationService migrationAiService;
    private MigrationService migrationAiKafkaService;

    /* 동시성 이슈를 해결하기 위해 ThreadLocal 사용 */
    private final ThreadLocal<MigrationService> currentService = ThreadLocal.withInitial(() -> migrationDefaultService);

    @Autowired
    public MigrationServiceSelector(@Qualifier("migrationDefaultService") MigrationService migrationDefaultService,
                                    @Qualifier("migrationAiKafkaService") MigrationService migrationAiKafkaService,
                                    @Qualifier("migrationAiService") MigrationService migrationAiService) {

        this.migrationDefaultService = migrationDefaultService;
        this.migrationAiService = migrationAiService;
        this.migrationAiKafkaService = migrationAiKafkaService;
    }


    public void useDefaultService() {
        currentService.set(migrationDefaultService);
    }

    public void useAiService() {
        currentService.set(migrationAiService);
    }

    public void useAiKafkaService() {
        currentService.set(migrationAiKafkaService);
    }

    public MigrationService getCurrentService() {
        return currentService.get();
    }

    public void clearCurrentService() {
        currentService.remove();
    }


}
