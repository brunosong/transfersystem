

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- data-migration-service 가 쓰는 DB. 표는 그 서비스의 Flyway 가 만든다
CREATE DATABASE datamigration;
