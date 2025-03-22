DROP SCHEMA IF EXISTS datamigration CASCADE;

CREATE SCHEMA datamigration;

DROP TABLE IF EXISTS datamigrations CASCADE;

CREATE TABLE datamigrations (
    datamigration_id BIGSERIAL PRIMARY KEY
);
