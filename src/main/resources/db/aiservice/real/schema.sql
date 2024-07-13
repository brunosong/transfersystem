create table ai_chap (
 ai_chap_seq bigint not null,
 ai_chap_title varchar(255),
 ai_chap_type varchar(255),
 primary key (ai_chap_seq)
) engine=InnoDB;


create table ai_course (
   ai_course_seq bigint not null,
   ai_course_name varchar(255),
   primary key (ai_course_seq)
) engine=InnoDB;