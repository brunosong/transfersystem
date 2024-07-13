package com.brunosong.transfersystem.main.service;

import com.brunosong.transfersystem.aiservice.service.MigrationAiService;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { MigrationServiceSelectorTest.Config.class , MigrationServiceSelector.class })
class MigrationServiceSelectorTest {

    @Autowired
    MigrationServiceSelector migrationServiceSelector;

    @Test
    void 스레드가_동시에_접근해도_항상_같은_서비스를_리턴한다() throws InterruptedException {

        int numberOfThreads = 2;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(numberOfThreads);
        AtomicReference<Class<? extends MigrationService>> initialServiceClassA = new AtomicReference<>();
        AtomicReference<Class<? extends MigrationService>> initialServiceClassB = new AtomicReference<>();

        Runnable runnableA = () -> {
            try {
                startLatch.await();
                log.info("Start : useAiService");

                // given
                migrationServiceSelector.useAiService();

                Class<? extends MigrationService> aiService = migrationServiceSelector.getCurrentService().getClass();
                if(initialServiceClassA.get() == null) initialServiceClassA.set(aiService);

                assertThat(initialServiceClassA.get()).isEqualTo(aiService);

                //when
                Thread.sleep(1000);
                aiService = migrationServiceSelector.getCurrentService().getClass();

                // then
                assertThat(initialServiceClassA.get()).isEqualTo(aiService);

                // 테스트가 실패하면 렌치를 누르지 못하게 된다.
                endLatch.countDown();

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };

        Runnable runnableB = () -> {
            log.info("useAiKafkaService");
            try {
                startLatch.await();

                // given
                migrationServiceSelector.useAiKafkaService();

                Class<? extends MigrationService> aiKafkaService = migrationServiceSelector.getCurrentService().getClass();
                if(initialServiceClassB.get() == null) initialServiceClassB.set(aiKafkaService);

                assertThat(initialServiceClassB.get()).isEqualTo(aiKafkaService);

                // when
                Thread.sleep(1000);
                aiKafkaService = migrationServiceSelector.getCurrentService().getClass();

                assertThat(initialServiceClassB.get()).isEqualTo(aiKafkaService);

                // 테스트가 실패하면 렌치를 누르지 못하게 된다.
                endLatch.countDown();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };

        Thread threadA = new Thread(runnableA);
        Thread threadB = new Thread(runnableB);

        threadA.start();
        threadB.start();

        /* 모든 스레드를 한번에 시작하기 위해서 스레드 안에 await()으로 기다리게 함 */
        startLatch.countDown(); // 모든 스레드 시작
        boolean await = endLatch.await(4, TimeUnit.SECONDS);

        if(!await) {
            throw new RuntimeException("테스트 실패");
        }

    }


    @TestConfiguration
    static class Config {

        @Bean("migrationAiService")
        public MigrationService migrationAiService() {
            return new MigrationAiService(null,null);
        }

        @Bean("migrationAiKafkaService")
        public MigrationService migrationAiKafkaService() {
            return new MigrationAiKafkaService(new KafkaTemplate<>(() -> { return null; }));
        }

        @Bean("migrationDefaultService")
        public MigrationService migrationDefaultService() {
            return new MigrationDefaultService();
        }

    }


}