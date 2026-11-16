package com.brunosong.deployment.system.deployment.service.dataaccess.deployment.adapter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 어드바이저리 락이 실제 Postgres 에서 "한 번에 한 쪽만" 돌리는지 본다.
 *
 * 인스턴스 두 대는 스레드 두 개로 흉내 낸다. 스레드마다 커넥션 풀에서 다른 커넥션(= 다른 세션)을 받으므로
 * Postgres 입장에서는 서로 다른 인스턴스와 같다.
 * 가짜 DB 로는 볼 수 없는 것이라 Testcontainers 를 쓴다.
 * 스케줄러가 락을 못 잡았을 때 건너뛰는지는 DeploymentOutboxSchedulerTest 가 본다.
 */
@JdbcTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = PostgresPublishLockTest.Config.class)
// @JdbcTest 는 테스트마다 트랜잭션을 연다. 그러면 테스트 스레드가 커넥션 하나를 계속 쥐어서 흉내가 흐려지므로 끈다
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class PostgresPublishLockTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18-alpine");

    @Configuration
    @Import(PostgresPublishLock.class)
    static class Config {
    }

    @Autowired
    PostgresPublishLock publishLock;

    @Autowired
    JdbcTemplate jdbcTemplate;

    /**
     * 확인하는 것: 한쪽이 락을 쥔 동안 다른 쪽은 기다리지 않고 바로 false 를 받고, 작업도 돌지 않는다.
     * 앞쪽이 끝나면 다시 잡을 수 있다.
     *
     * 이것이 틀리면 두 인스턴스가 같은 주기에 같은 행을 보내 메시지가 두 번 나가거나 순서가 뒤집힌다.
     */
    @Test
    @DisplayName("한쪽이 발행 중이면 다른 쪽은 바로 건너뛰고, 끝나면 다시 잡힌다")
    void onlyOneRunsAtATime() throws Exception {
        CountDownLatch locked = new CountDownLatch(1);    // 첫 번째가 락을 잡았다는 신호
        CountDownLatch release = new CountDownLatch(1);   // 첫 번째에게 이제 끝내라는 신호
        ExecutorService otherInstance = Executors.newSingleThreadExecutor();

        // 준비: 다른 스레드(인스턴스 1)가 락을 잡고, 신호가 올 때까지 발행 중인 척 머문다
        Future<Boolean> first = otherInstance.submit(() -> publishLock.runIfFree(() -> {
            locked.countDown();
            await(release);
        }));
        assertTrue(locked.await(5, TimeUnit.SECONDS), "첫 번째가 락을 잡지 못했다");

        // 실행 1: 그동안 이 스레드(인스턴스 2)가 락을 잡으려 한다
        AtomicBoolean secondRan = new AtomicBoolean(false);
        boolean secondGot = publishLock.runIfFree(() -> secondRan.set(true));

        // 확인 1: 기다리지 않고 바로 false 이고, 작업은 돌지 않았다
        assertFalse(secondGot);
        assertFalse(secondRan.get());

        // 실행 2: 첫 번째를 끝낸다. 첫 번째는 작업을 돌렸으니 true
        release.countDown();
        assertTrue(first.get(5, TimeUnit.SECONDS));
        otherInstance.shutdown();

        // 확인 2: 풀린 뒤에는 다시 잡히고 작업이 돈다
        assertTrue(publishLock.runIfFree(() -> secondRan.set(true)));
        assertTrue(secondRan.get());
    }

    /**
     * 확인하는 것: 작업이 예외를 던져도 락은 풀린다.
     *
     * 풀리지 않으면 커넥션이 풀로 돌아간 뒤에도 그 세션이 락을 쥐고 있어,
     * 그 인스턴스가 살아 있는 동안 아무도 발행하지 못할 수 있다.
     * 같은 세션은 같은 락을 여러 번 잡을 수 있어서 "다시 잡히는지"로는 확인할 수 없다.
     * 그래서 Postgres 의 락 목록(pg_locks)에 어드바이저리 락이 남았는지 직접 센다.
     */
    @Test
    @DisplayName("작업이 실패해도 락은 풀린다")
    void lockIsReleasedWhenTaskFails() {
        // 실행: 발행 중에 예외가 난 상황
        assertThrows(IllegalStateException.class,
                () -> publishLock.runIfFree(() -> { throw new IllegalStateException("발행 중 오류"); }));

        // 확인: 남아 있는 어드바이저리 락이 없다
        Integer held = jdbcTemplate.queryForObject(
                "select count(*) from pg_locks where locktype = 'advisory'", Integer.class);
        assertEquals(0, held);
    }

    /** 신호를 최대 5초 기다린다. 테스트가 영원히 멈추지 않게 시간을 둔다 */
    private static void await(CountDownLatch latch) {
        try {
            latch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}