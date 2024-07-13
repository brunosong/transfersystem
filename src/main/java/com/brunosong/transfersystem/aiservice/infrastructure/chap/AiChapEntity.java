package com.brunosong.transfersystem.aiservice.infrastructure.chap;

import com.brunosong.transfersystem.aiservice.domain.AiChap;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "ai_chap")
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AiChapEntity {

    @Id
    @Column(name = "ai_chap_seq")
    private Long aiChapSeq;

    @Column(name = "ai_chap_title")
    private String aiChapTitle;

    @Column(name = "ai_chap_type")
    private String aiChapType;

    public AiChap toModel(){
        return com.brunosong.transfersystem.aiservice.domain.AiChap.builder()
            .aiChapSeq(this.getAiChapSeq())
            .aiChapTitle(this.getAiChapTitle())
            .aiChapType(AiChap.AiChapTypeEnum.valueOf(this.getAiChapType()))
            .build();
    }

    public static AiChapEntity fromModel(AiChap aiChap) {
        return AiChapEntity.builder()
                .aiChapSeq(aiChap.getAiChapSeq())
                .aiChapTitle(aiChap.getAiChapTitle())
                .aiChapType(aiChap.getAiChapType().name())
                .build();
    }

}
