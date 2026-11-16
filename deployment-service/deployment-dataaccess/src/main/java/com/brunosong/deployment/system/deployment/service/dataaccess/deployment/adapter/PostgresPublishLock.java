package com.brunosong.deployment.system.deployment.service.dataaccess.deployment.adapter;

import com.brunosong.deployment.system.deployment.service.ports.output.lock.PublishLock;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Postgres 세션 어드바이저리 락으로 한 대만 발행하게 한다.
 *
 * 세션 락은 커넥션에 붙는다. 그래서 커넥션 하나를 빌려, 그 커넥션에서 잡고 작업을 돌린 뒤 같은 커넥션에서 푼다.
 * 다른 커넥션에서 풀면 풀리지 않는다.
 * 인스턴스가 죽으면 커넥션이 끊기면서 락이 저절로 풀린다. 그래서 다른 인스턴스가 이어받는다.
 * 트랜잭션 락(pg_try_advisory_xact_lock)을 쓰지 않는 이유: 스케줄러는 행마다 따로 커밋해서 주기 전체를 감싸는 트랜잭션이 없다.
 */
@Component
@RequiredArgsConstructor
public class PostgresPublishLock implements PublishLock {

    /** 락 이름. 다른 일에 락을 쓰게 되면 다른 이름을 써서 키가 겹치지 않게 한다 */
    static final String LOCK_NAME = "deployment-outbox-publisher";
    /** 어드바이저리 락의 키는 숫자다. 이름의 hashCode 를 쓴다. String.hashCode 는 JVM 이 달라도 같은 값이다 */
    static final long LOCK_KEY = LOCK_NAME.hashCode();

    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean runIfFree(Runnable task) {
        Boolean ran = jdbcTemplate.execute((ConnectionCallback<Boolean>) connection -> {
            if (!tryLock(connection)) {
                return false;
            }
            try {
                task.run();
                return true;
            } finally {
                unlock(connection);
            }
        });
        return Boolean.TRUE.equals(ran);
    }

    /** 바로 잡히면 true, 다른 세션이 쥐고 있으면 기다리지 않고 false */
    private boolean tryLock(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("select pg_try_advisory_lock(?)")) {
            statement.setLong(1, LOCK_KEY);
            try (ResultSet result = statement.executeQuery()) {
                result.next();
                return result.getBoolean(1);
            }
        }
    }

    private void unlock(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("select pg_advisory_unlock(?)")) {
            statement.setLong(1, LOCK_KEY);
            statement.execute();
        }
    }
}
