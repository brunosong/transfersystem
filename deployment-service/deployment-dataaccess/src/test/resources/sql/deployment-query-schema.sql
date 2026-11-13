-- 조회 어댑터 테스트용 스키마.
-- 운영 스키마는 지금 docker 의 postgres 초기화 스크립트(열거형 타입)와
-- deployment-container 의 init-schema.sql(테이블)로 나뉘어 있다.
-- Phase 2 에서 Flyway 마이그레이션으로 합치면 이 파일 대신 그것을 쓴다.

DROP TABLE IF EXISTS deployment_chunk_payload CASCADE;
DROP TABLE IF EXISTS deployment_chunk CASCADE;
DROP TABLE IF EXISTS deployment_chunks CASCADE;
DROP TABLE IF EXISTS deployment CASCADE;
DROP TABLE IF EXISTS sourceconfig CASCADE;
DROP TYPE IF EXISTS source_type;
DROP TYPE IF EXISTS db_type;

CREATE TYPE source_type AS ENUM ('CURRICULUM', 'EXAM_RESULT_DATA', 'LEARNING_MATERIAL', 'USER_DATA');
CREATE TYPE db_type AS ENUM ('MONGO', 'MYSQL', 'POSTGRESQL', 'ORACLE', 'H2');

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

CREATE TABLE deployment (
    id UUID PRIMARY KEY,
    source_id VARCHAR(100) NOT NULL,
    sourceconfig_id BIGINT NOT NULL,
    source_type source_type NOT NULL,
    target_id BIGINT NOT NULL,
    db_type db_type NOT NULL,
    trans_type VARCHAR(50) NOT NULL,
    deployment_status VARCHAR(50) NOT NULL,
    create_admin_id VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    result_message VARCHAR(1000),
    total_chunk_size BIGINT,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE deployment_chunks (
    id BIGSERIAL PRIMARY KEY,
    deployment_id UUID NOT NULL REFERENCES deployment (id) ON DELETE CASCADE,
    chunk_offset BIGINT NOT NULL,
    size INTEGER NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_deployment_chunk UNIQUE (deployment_id, chunk_offset)
);

CREATE TABLE deployment_chunk (
  deployment_id   UUID          NOT NULL REFERENCES deployment (id) ON DELETE CASCADE,
  chunk_no        INTEGER       NOT NULL,
  status          VARCHAR(20)   NOT NULL,
  attempt         INTEGER       NOT NULL,
  sent_at         TIMESTAMP WITH TIME ZONE,
  failure_kind    VARCHAR(20),
  failure_message VARCHAR(1000),
  failed_at       TIMESTAMP WITH TIME ZONE,
  PRIMARY KEY (deployment_id, chunk_no)
);

CREATE TABLE deployment_chunk_payload (
  deployment_id UUID    NOT NULL REFERENCES deployment (id) ON DELETE CASCADE,
  chunk_no      INTEGER NOT NULL,
  content       JSONB   NOT NULL,
  PRIMARY KEY (deployment_id, chunk_no)
);


-- 원본 설정. 코드의 SourceType 에 없는 값(LEARNING_MATERIAL)이 섞여 있어도 목록이 깨지지 않아야 한다
INSERT INTO sourceconfig (source_type, db_type, host, port, database, username, password, table_or_collection) VALUES
    ('CURRICULUM', 'MONGO', 'mongo', 27017, 'learningDB', 'reader', 'secret', 'CurriculumData'),
    ('LEARNING_MATERIAL', 'MONGO', 'mongo', 27017, 'exampledb', 'root', 'example', 'learning_materials');

-- 배포 세 건: 진행 중(청크 5개 중 2개 성공), 실패(1개 실패), 성공
INSERT INTO deployment (id, source_id, sourceconfig_id, source_type, target_id, db_type, trans_type,
                          deployment_status, create_admin_id, created_at, updated_at, total_chunk_size) VALUES
    ('11111111-1111-1111-1111-111111111111', 'curriculum-001', 1, 'CURRICULUM', 1, 'MONGO', 'MESSAGING',
     'SENT', 'brunosong', '2026-11-01 10:00:00', '2026-11-01 10:00:05', 5),
    ('22222222-2222-2222-2222-222222222222', 'curriculum-002', 1, 'CURRICULUM', 2, 'MONGO', 'MESSAGING',
     'FAILED', 'brunosong', '2026-11-01 11:00:00', '2026-11-01 11:00:09', 3),
    ('33333333-3333-3333-3333-333333333333', 'curriculum-003', 1, 'CURRICULUM', 1, 'MONGO', 'MESSAGING',
     'SUCCESS', 'brunosong', '2026-11-01 12:00:00', '2026-11-01 12:00:03', 2);

INSERT INTO deployment_chunks (deployment_id, chunk_offset, size, status) VALUES
    ('11111111-1111-1111-1111-111111111111', 2, 5, 'Success'),
    ('11111111-1111-1111-1111-111111111111', 1, 5, 'Success'),
    ('22222222-2222-2222-2222-222222222222', 1, 3, 'Failed'),
    ('33333333-3333-3333-3333-333333333333', 1, 2, 'Success'),
    ('33333333-3333-3333-3333-333333333333', 2, 2, 'Success');
