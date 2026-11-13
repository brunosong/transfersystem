-- 청크 내용을 보관한다. 과목은 뼈대 결과가 온 뒤에 보내므로 그때까지 내용이 남아 있어야 한다(불변식 3).
-- 재전송도 같은 내용을 다시 보낸다(불변식 4). 원본을 다시 읽지 않는다.
-- 처음에는 JSON 으로 둔다. 이유는 시스템 설계 6장 "청크 내용은 어디에 두나".
CREATE TABLE deployment_chunk_payload (
      deployment_id UUID    NOT NULL,
      chunk_no      INTEGER NOT NULL,
      content       JSONB   NOT NULL,
      CONSTRAINT deployment_chunk_payload_pkey PRIMARY KEY (deployment_id, chunk_no),
      CONSTRAINT fk_deployment_chunk_payload_deployment
      FOREIGN KEY (deployment_id) REFERENCES deployment (id) ON DELETE CASCADE
);