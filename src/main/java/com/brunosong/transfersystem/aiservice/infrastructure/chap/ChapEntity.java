package com.brunosong.transfersystem.aiservice.infrastructure.chap;

import lombok.*;

import javax.persistence.*;


@Entity
@Table(name = "bruno_chap")
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChapEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chap_seq")
    private Long chapSeq;

    @Column(name = "chap_title")
    private String chapTitle;

}
