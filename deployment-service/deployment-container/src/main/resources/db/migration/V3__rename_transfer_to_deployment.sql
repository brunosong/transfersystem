-- 용어를 배포(Deployment)로 맞춘다. 이미 적용된 V1 을 고치면 체크섬이 어긋나므로 새 버전으로 바꾼다.
ALTER TABLE transfer_log RENAME TO deployment;
ALTER TABLE deployment RENAME COLUMN transfer_status TO deployment_status;
ALTER INDEX transfer_log_pkey RENAME TO deployment_pkey;

ALTER TABLE transfer_chunks RENAME TO deployment_chunks;
ALTER TABLE deployment_chunks RENAME COLUMN transfer_log_id TO deployment_id;
ALTER TABLE deployment_chunks RENAME CONSTRAINT fk_transfer_log TO fk_deployment;
ALTER TABLE deployment_chunks RENAME CONSTRAINT uk_transfer_chunk TO uk_deployment_chunk;
ALTER INDEX transfer_chunks_pkey RENAME TO deployment_chunks_pkey;
ALTER SEQUENCE transfer_chunks_id_seq RENAME TO deployment_chunks_id_seq;

ALTER TABLE transfer_outbox RENAME TO deployment_outbox;
ALTER INDEX transfer_outbox_pkey RENAME TO deployment_outbox_pkey;
ALTER INDEX transfer_outbox_saga_status RENAME TO deployment_outbox_saga_status;
ALTER INDEX transfer_outbox_saga_id RENAME TO deployment_outbox_saga_id;
