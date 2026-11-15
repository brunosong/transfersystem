-- 아웃박스 행을 넣은 순서대로 꺼내려고 순번을 둔다(ADR 0004).
-- 기본 키는 무작위 UUID 라 순서가 없고, created_at 은 한 배포의 행이 모두 같은 시각이다.
-- 이미 있는 행에도 번호가 매겨진다.
ALTER TABLE deployment_outbox ADD COLUMN seq BIGSERIAL;

-- 스케줄러가 읽는 조건(type, outbox_status)과 정렬(seq)에 맞춰 인덱스를 바꾼다
DROP INDEX IF EXISTS deployment_outbox_type_status;
CREATE INDEX deployment_outbox_type_status_seq ON deployment_outbox (type, outbox_status, seq);