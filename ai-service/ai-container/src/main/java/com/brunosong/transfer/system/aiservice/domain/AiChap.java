package com.brunosong.transfer.system.aiservice.domain;


import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class AiChap {

    private Long aiChapSeq;
    private String aiChapTitle;
    private AiChapTypeEnum aiChapType;

    public void updateTitle(String title) {
        this.aiChapTitle = title;
    }

    public void updateChapInfo(AiChap aiChap) {
        this.aiChapTitle = aiChap.getAiChapTitle();
        this.aiChapType = aiChap.getAiChapType();
    }

    public enum AiChapTypeEnum {
        KOR,
        ENG,
        MATH
    }

}
