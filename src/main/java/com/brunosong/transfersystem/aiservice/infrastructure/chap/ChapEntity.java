package com.brunosong.transfersystem.aiservice.infrastructure.chap;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "bruno_chap")
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChapEntity {

    @Id
    @Column(name = "chap_seq")
    private Long chapSeq;

    @Column(name = "chap_title")
    private String chapTitle;

    @Column(name = "chap_type")
    private String chapType;

}
