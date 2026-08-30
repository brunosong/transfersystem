

-- sourceconfig 테이블
DROP TABLE IF EXISTS sourceconfig CASCADE;
CREATE TABLE sourceconfig (
    sourceconfig_id BIGSERIAL PRIMARY KEY,
    source_type source_type NOT NULL,
    db_type db_type NOT NULL,
    host VARCHAR(255) NOT NULL,
    port INTEGER NOT NULL,
    database VARCHAR(100) NOT NULL,
    username VARCHAR(100),
    password VARCHAR(100),
    table_or_collection VARCHAR(100) NOT NULL
);

INSERT INTO sourceconfig (source_type, db_type, host, port, database, username, password, table_or_collection) VALUES
    ('LEARNING_MATERIAL', 'MONGO', 'localhost', 27017, 'exampledb', 'root', 'example', 'learning_materials'),
    ('USER_DATA', 'MYSQL', 'localhost', 3306, 'transfer', 'brunosong', '1234', 'users'),
    ('LEARNING_MATERIAL', 'MONGO', 'localhost', 27017, 'exampledb', 'learning_user', 'learning_pass123', 'learning_materials'),
    ('CURRICULUM', 'MONGO', 'localhost', 27017, 'learningDB', 'transfer_mongo_learning_user', 'asdf1234', 'CurriculumData');

-- transfer 로그 테이블
DROP TABLE IF EXISTS transfer_log CASCADE;
CREATE TABLE transfer_log (
    id UUID PRIMARY KEY,
    source_id VARCHAR(100) NOT NULL,
    sourceconfig_id BIGSERIAL NOT NULL,
    source_type source_type NOT NULL,
    datamigration_id BIGSERIAL NOT NULL,
    db_type db_type NOT NULL,
    trans_type VARCHAR(50) NOT NULL,
    transfer_status VARCHAR(50) NOT NULL,
    create_admin_id VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    result_message VARCHAR(1000),
    total_chunk_size BIGSERIAL
);


-- transfer_chunks 테이블
DROP TABLE IF EXISTS transfer_chunks CASCADE;
CREATE TABLE transfer_chunks (
    id BIGSERIAL PRIMARY KEY,
    transfer_log_id UUID NOT NULL,
    chunk_offset BIGINT NOT NULL,
    size INTEGER NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transfer_log
        FOREIGN KEY (transfer_log_id)
        REFERENCES transfer_log(id)
        ON DELETE CASCADE,
    -- 메시지는 최소 한 번 전달되므로 같은 청크가 두 번 올 수 있다.
    -- 마지막 방어선을 DB 에 둔다
    CONSTRAINT uk_transfer_chunk UNIQUE (transfer_log_id, chunk_offset)
);




-- outbox 테이블
DROP TABLE IF EXISTS transfer_outbox CASCADE;
CREATE TABLE transfer_outbox (
    id UUID NOT NULL,
    saga_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    processed_at TIMESTAMP WITH TIME ZONE,
    type character varying COLLATE pg_catalog."default" NOT NULL,
    trans_type VARCHAR(50) NOT NULL,
    payload jsonb NOT NULL,
    saga_status VARCHAR(20) NOT NULL,
    outbox_status VARCHAR(20) NOT NULL,
    version integer NOT NULL,
    CONSTRAINT transfer_outbox_pkey PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS transfer_outbox_saga_status ON transfer_outbox (type, outbox_status, saga_status);
CREATE INDEX IF NOT EXISTS transfer_outbox_saga_id ON transfer_outbox (type, saga_id, saga_status);
