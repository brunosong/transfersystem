package com.brunosong.transfersystem.aiservice.infrastructure.chap;

import com.brunosong.transfersystem.aiservice.domain.Chap;
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

    public Chap toModel(){
        return Chap.builder()
            .chapSeq(this.getChapSeq())
            .chapTitle(this.getChapTitle())
            .chapType(Chap.ChapTypeEnum.valueOf(this.getChapType()))
            .build();
    }

    public static ChapEntity fromModel(Chap chap) {
        return ChapEntity.builder()
                .chapSeq(chap.getChapSeq())
                .chapTitle(chap.getChapTitle())
                .chapType(chap.getChapType().name())
                .build();
    }

}
