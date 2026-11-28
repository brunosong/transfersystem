-- 온라인캠퍼스 대상 DB의 표. PROD와 DEV에 똑같이 만든다.
-- 전에는 Hibernate(hbm2ddl)가 시작할 때 기본 DB(DEV) 하나에만 만들어서, PROD에는 표가 없었다.
-- H2(MySQL 모드)와 MySQL에서 함께 돌도록 쓴다

CREATE TABLE curriculum (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    curriculum_id VARCHAR(255) NOT NULL,
    title         VARCHAR(255) NOT NULL,
    description   VARCHAR(255),
    CONSTRAINT uk_curriculum_curriculum_id UNIQUE (curriculum_id)
);

CREATE TABLE grade (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    grade_id      VARCHAR(255) NOT NULL,
    title         VARCHAR(255) NOT NULL,
    description   VARCHAR(255),
    curriculum_id BIGINT,
    CONSTRAINT uk_grade_grade_id UNIQUE (grade_id),
    CONSTRAINT fk_grade_curriculum FOREIGN KEY (curriculum_id) REFERENCES curriculum (id)
);

CREATE TABLE semester (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    semester_id VARCHAR(255) NOT NULL,
    title       VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    grade_id    BIGINT,
    CONSTRAINT uk_semester_semester_id UNIQUE (semester_id),
    CONSTRAINT fk_semester_grade FOREIGN KEY (grade_id) REFERENCES grade (id)
);

CREATE TABLE subject (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    subject_id  VARCHAR(255) NOT NULL,
    title       VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    semester_id BIGINT,
    CONSTRAINT uk_subject_subject_id UNIQUE (subject_id),
    CONSTRAINT fk_subject_semester FOREIGN KEY (semester_id) REFERENCES semester (id)
);

CREATE TABLE unit (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    unit_id     VARCHAR(255) NOT NULL,
    title       VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    subject_id  BIGINT,
    CONSTRAINT uk_unit_unit_id UNIQUE (unit_id),
    CONSTRAINT fk_unit_subject FOREIGN KEY (subject_id) REFERENCES subject (id)
);

CREATE TABLE lesson (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    lesson_id      VARCHAR(255) NOT NULL,
    title          VARCHAR(255) NOT NULL,
    description    VARCHAR(255),
    lesson_number  INT NOT NULL,
    learning_level INT NOT NULL,
    unit_id        BIGINT,
    CONSTRAINT uk_lesson_lesson_id UNIQUE (lesson_id),
    CONSTRAINT fk_lesson_unit FOREIGN KEY (unit_id) REFERENCES unit (id)
);
