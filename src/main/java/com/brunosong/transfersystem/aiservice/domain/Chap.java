package com.brunosong.transfersystem.aiservice.domain;


import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class Chap {

    private Long chapSeq;
    private String chapTitle;

    private ChapTypeEnum chapType;

    public void updateTitle(String title) {
        this.chapTitle = title;
    }

    public void updateChapInfo(Chap chap) {
        this.chapTitle = chap.getChapTitle();
        this.chapType = chap.getChapType();
    }

    public enum ChapTypeEnum {
        KOR,
        ENG,
        MATH
    }

}
