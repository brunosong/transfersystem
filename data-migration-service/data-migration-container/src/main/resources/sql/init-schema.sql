DROP SCHEMA IF EXISTS datamigration CASCADE;

CREATE SCHEMA datamigration;

DROP TYPE IF EXISTS target_system_environment;
CREATE TYPE target_system_environment AS ENUM ('PROD', 'DEV');

DROP TABLE IF EXISTS "datamigration".datamigrations CASCADE;
CREATE TABLE "datamigration".datamigrations (
    datamigration_id BIGSERIAL PRIMARY KEY,
    target_system VARCHAR(100) NOT NULL,
    target_system_environment target_system_environment NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

INSERT INTO "datamigration".datamigrations (
    target_system,
    target_system_environment,
    created_at,
    updated_at
) VALUES
  ('BRUNOSONG_ONLINE_CAMPUS', 'PROD', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('BRUNOSONG_BOOTCAMP', 'PROD', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('BRUNOSONG_ONLINE_CAMPUS', 'DEV', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('BRUNOSONG_BOOTCAMP', 'DEV', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


