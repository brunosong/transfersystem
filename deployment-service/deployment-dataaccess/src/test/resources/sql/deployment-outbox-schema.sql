-- 아웃박스 저장소 테스트용. 운영(Flyway V1, V6, V7, V8, V11, V14)을 거친 뒤의 모양과 같다
DROP TABLE IF EXISTS deployment_outbox;

CREATE TABLE deployment_outbox (
    id            UUID PRIMARY KEY,
    seq           BIGSERIAL,
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    processed_at  TIMESTAMP WITH TIME ZONE,
    type          VARCHAR(100) NOT NULL,
    trans_type    VARCHAR(50)  NOT NULL,
    payload       JSONB        NOT NULL,
    message_key   VARCHAR(200) NOT NULL,
    outbox_status VARCHAR(20)  NOT NULL
);