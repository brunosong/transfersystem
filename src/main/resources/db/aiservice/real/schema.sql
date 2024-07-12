create table bruno_chap (
    chap_seq bigint not null,
    chap_title varchar(255),
    chap_type varchar(255),
    primary key (chap_seq)
) engine=InnoDB ;