-- 용어를 대상(Target)으로 맞춘다.
ALTER TABLE deployment RENAME COLUMN datamigration_id TO target_id;
