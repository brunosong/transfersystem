-- 용어를 대상(Target)으로 맞춘다. DB 와 스키마 이름 datamigration 은 그대로 둔다.
ALTER TABLE datamigrations RENAME TO targets;
ALTER TABLE targets RENAME COLUMN datamigration_id TO target_id;
ALTER INDEX datamigrations_pkey RENAME TO targets_pkey;
ALTER SEQUENCE datamigrations_datamigration_id_seq RENAME TO targets_target_id_seq;
