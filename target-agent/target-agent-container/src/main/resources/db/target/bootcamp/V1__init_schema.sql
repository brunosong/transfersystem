-- 부트캠프 대상 DB의 표. PROD와 DEV에 똑같이 만든다.
-- 온라인캠퍼스와 일부러 모양이 다르다. 학년과 학기를 기수(term) 하나로, 단원은 차시(chapter)의 칸으로 납작하게 둔다.
-- 대상마다 저장 모양이 달라도 에이전트가 계약을 자기 모양으로 바꿔 넣는다(ADR 0002)
-- H2(MySQL 모드)와 MySQL에서 함께 돌도록 쓴다

CREATE TABLE program (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    curriculum_id VARCHAR(255) NOT NULL,
    title         VARCHAR(255) NOT NULL,
    description   VARCHAR(255),
    CONSTRAINT uk_program_curriculum_id UNIQUE (curriculum_id)
);

-- 학년 + 학기. term_id 는 원본의 학기 ID다
CREATE TABLE term (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    term_id       VARCHAR(255) NOT NULL,
    curriculum_id VARCHAR(255) NOT NULL,
    title         VARCHAR(255) NOT NULL,
    term_order    INT NOT NULL,
    CONSTRAINT uk_term_term_id UNIQUE (term_id),
    CONSTRAINT fk_term_program FOREIGN KEY (curriculum_id) REFERENCES program (curriculum_id)
);

-- 과목. course_id 는 원본의 과목 ID다
CREATE TABLE course (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id   VARCHAR(255) NOT NULL,
    term_id     VARCHAR(255) NOT NULL,
    title       VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    CONSTRAINT uk_course_course_id UNIQUE (course_id),
    CONSTRAINT fk_course_term FOREIGN KEY (term_id) REFERENCES term (term_id)
);

-- 차시. 단원은 따로 두지 않고 unit_title 로만 남긴다. chapter_order 는 과목 안의 순서다
CREATE TABLE chapter (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    chapter_id     VARCHAR(255) NOT NULL,
    course_id      VARCHAR(255) NOT NULL,
    unit_title     VARCHAR(255) NOT NULL,
    title          VARCHAR(255) NOT NULL,
    chapter_order  INT NOT NULL,
    learning_level INT NOT NULL,
    CONSTRAINT uk_chapter_chapter_id UNIQUE (chapter_id),
    CONSTRAINT fk_chapter_course FOREIGN KEY (course_id) REFERENCES course (course_id)
);
