-- 사가를 쓰지 않기로 해서 아웃박스의 사가 칸(saga_id, saga_status)을 지운다.
-- saga_id 는 이름과 달리 청크마다 새로 만든 무작위 값이었고, Kafka 키로만 쓰였다.
-- 칸을 지우면 그 칸이 들어간 인덱스도 함께 지워지지만, 무엇이 없어지는지 보이게 먼저 지운다.
DROP INDEX IF EXISTS deployment_outbox_saga_id;
DROP INDEX IF EXISTS deployment_outbox_saga_status;

ALTER TABLE deployment_outbox DROP COLUMN saga_id;
ALTER TABLE deployment_outbox DROP COLUMN saga_status;

-- 스케줄러가 읽는 조건(type, outbox_status)에 맞춰 인덱스를 다시 만든다.
CREATE INDEX deployment_outbox_type_status ON deployment_outbox (type, outbox_status);
