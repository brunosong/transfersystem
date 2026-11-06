-- 원본 저장소
CREATE TABLE sourceconfig (
    sourceconfig_id     BIGSERIAL PRIMARY KEY,
    source_type         VARCHAR(50)  NOT NULL,
    db_type             VARCHAR(50)  NOT NULL,
    host                VARCHAR(255) NOT NULL,
    port                INTEGER      NOT NULL,
    database            VARCHAR(100) NOT NULL,
    username            VARCHAR(100),
    password            VARCHAR(100),
    table_or_collection VARCHAR(100) NOT NULL
);

-- 배포 기록
CREATE TABLE transfer_log (
    id               UUID PRIMARY KEY,
    source_id        VARCHAR(100) NOT NULL,
    sourceconfig_id  BIGINT       NOT NULL,
    source_type      VARCHAR(50)  NOT NULL,
    datamigration_id BIGINT       NOT NULL,
    db_type          VARCHAR(50)  NOT NULL,
    trans_type       VARCHAR(50)  NOT NULL,
    transfer_status  VARCHAR(50)  NOT NULL,
    create_admin_id  VARCHAR(100) NOT NULL,
    created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    result_message   VARCHAR(1000),
    total_chunk_size INTEGER      NOT NULL DEFAULT 0
);

-- 청크. 메시지는 최소 한 번 전달되므로 같은 청크가 두 번 올 수 있어 DB 에서도 막는다
CREATE TABLE transfer_chunks (
    id              BIGSERIAL PRIMARY KEY,
    transfer_log_id UUID        NOT NULL,
    chunk_offset    INTEGER     NOT NULL,
    size            INTEGER     NOT NULL,
    status          VARCHAR(50) NOT NULL,
    created_at      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transfer_log
     FOREIGN KEY (transfer_log_id) REFERENCES transfer_log (id) ON DELETE CASCADE,
    CONSTRAINT uk_transfer_chunk UNIQUE (transfer_log_id, chunk_offset)
);

-- 아웃박스
CREATE TABLE transfer_outbox (
    id           UUID PRIMARY KEY,
    saga_id      UUID         NOT NULL,
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    processed_at TIMESTAMP WITH TIME ZONE,
    type         VARCHAR(100) NOT NULL,
    trans_type   VARCHAR(50)  NOT NULL,
    payload      JSONB        NOT NULL,
    saga_status  VARCHAR(20)  NOT NULL,
    outbox_status VARCHAR(20) NOT NULL,
    version      INTEGER      NOT NULL
);


CREATE INDEX transfer_outbox_saga_status ON transfer_outbox (type, outbox_status, saga_status);
CREATE INDEX transfer_outbox_saga_id ON transfer_outbox (type, saga_id, saga_status);