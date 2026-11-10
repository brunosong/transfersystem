-- 저장 어댑터 테스트용 스키마. 운영(Flyway V1~V5)처럼 종류와 상태를 VARCHAR 로 둔다.
-- 조회 테스트 스키마(deployment-query-schema.sql)는 옛 열거형 타입을 써서 Hibernate 가 쓰는 문자열을 받지 못한다.
DROP TABLE IF EXISTS deployment_chunk CASCADE;
DROP TABLE IF EXISTS deployment CASCADE;


CREATE TABLE deployment (
    id                UUID PRIMARY KEY,
    source_id         VARCHAR(100) NOT NULL,
    sourceconfig_id   BIGINT       NOT NULL,
    source_type       VARCHAR(50)  NOT NULL,
    target_id         BIGINT       NOT NULL,
    db_type           VARCHAR(50)  NOT NULL,
    trans_type        VARCHAR(50)  NOT NULL,
    deployment_status VARCHAR(50)  NOT NULL,
    create_admin_id   VARCHAR(100) NOT NULL,
    created_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    result_message    VARCHAR(1000),
    total_chunk_size  INTEGER      NOT NULL DEFAULT 0,
    version           BIGINT       NOT NULL DEFAULT 0
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