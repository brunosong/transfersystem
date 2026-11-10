-- 청크는 접수할 때 만들고 결과가 오면 고친다.
-- 옛 표(deployment_chunks)는 결과가 와야 행이 생겨 "아직 안 보냄"과 "응답 없음"을 구별할 수 없다.
-- 조회 화면이 아직 옛 표를 읽으므로 옛 표는 조회를 옮길 때 지운다.
CREATE TABLE deployment_chunk (
      deployment_id   UUID          NOT NULL,
      chunk_no        INTEGER       NOT NULL,
      status          VARCHAR(20)   NOT NULL,
      attempt         INTEGER       NOT NULL,
      sent_at         TIMESTAMP WITH TIME ZONE,
      failure_kind    VARCHAR(20),
      failure_message VARCHAR(1000),
      failed_at       TIMESTAMP WITH TIME ZONE,
      CONSTRAINT deployment_chunk_pkey PRIMARY KEY (deployment_id, chunk_no),
      CONSTRAINT fk_deployment_chunk_deployment
          FOREIGN KEY (deployment_id) REFERENCES deployment (id) ON DELETE CASCADE
);

-- 응답 기한 스케줄러가 기다리는 청크만 읽는다
CREATE INDEX deployment_chunk_sent_at ON deployment_chunk (sent_at) WHERE status = 'SENT';

-- 같은 배포에 결과가 동시에 오면 낙관적 락으로 막는다
ALTER TABLE deployment ADD COLUMN version BIGINT NOT NULL DEFAULT 0;