CREATE TYPE target_system_environment AS ENUM ('PROD', 'DEV');

CREATE TABLE datamigrations (
    datamigration_id          BIGSERIAL PRIMARY KEY,
    target_system             VARCHAR(100)              NOT NULL,
    target_system_environment target_system_environment NOT NULL,
    created_at                TIMESTAMP                 NOT NULL,
    updated_at                TIMESTAMP                 NOT NULL
);
