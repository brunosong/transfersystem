package com.brunosong.transfersystem.main.service;

import com.brunosong.transfersystem.aiservice.service.TranActionAiServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { TranActionServiceSelectorTest.Config.class , TranActionServiceSelector.class })
class TranActionServiceSelectorTest {

    @Autowired
    TranActionServiceSelector tranActionServiceSelector;

    @Test
    void 스레드가_동시에_접근해도_항상_같은_서비스를_리턴한다() throws InterruptedException {

        int numberOfThreads = 2;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(numberOfThreads);
        AtomicReference<Class<? extends TranActionService>> initialServiceClassA = new AtomicReference<>();
        AtomicReference<Class<? extends TranActionService>> initialServiceClassB = new AtomicReference<>();

        Runnable runnableA = () -> {
            try {
                startLatch.await();
                log.info("Start : useAiService");

                // given
                tranActionServiceSelector.useAiService();

                Class<? extends TranActionService> aiService = tranActionServiceSelector.getCurrentService().getClass();
                if(initialServiceClassA.get() == null) initialServiceClassA.set(aiService);

                assertThat(initialServiceClassA.get()).isEqualTo(aiService);

                //when
                Thread.sleep(1000);
                aiService = tranActionServiceSelector.getCurrentService().getClass();

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
                tranActionServiceSelector.useAiKafkaService();

                Class<? extends TranActionService> aiKafkaService = tranActionServiceSelector.getCurrentService().getClass();
                if(initialServiceClassB.get() == null) initialServiceClassB.set(aiKafkaService);

                assertThat(initialServiceClassB.get()).isEqualTo(aiKafkaService);

                // when
                Thread.sleep(1000);
                aiKafkaService = tranActionServiceSelector.getCurrentService().getClass();

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

        @Bean("tranActionAiService")
        public TranActionService tranActionAiService() {
            return new TranActionAiServiceImpl();
        }

        @Bean("tranActionAiKafkaService")
        public TranActionService tranActionAiKafkaService() {
            return new TranActionAiKafkaServiceImpl();
        }

        @Bean("tranActionDefaultService")
        public TranActionService tranActionDefaultService() {
            return new TranActionDefaultServiceImpl();
        }

    }


}