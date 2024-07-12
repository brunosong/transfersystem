package com.brunosong.transfersystem.main.dto;

import com.brunosong.transfersystem.main.domain.chap.MainChap;
import lombok.Getter;
import lombok.Setter;

public class TranDto {

    @Getter
    @Setter
    public static class ChapTranDto {

        private Long chapSeq;
        private String chapTitle;
        private String chapType;

        public static ChapTranDto fromEntity(MainChap mainChap) {
            ChapTranDto chapTranDto = new ChapTranDto();
            chapTranDto.setChapSeq(mainChap.getChapSeq());
            chapTranDto.setChapTitle(mainChap.getChapTitle());
            chapTranDto.setChapType(mainChap.getChapType().name());
            return chapTranDto;
        }

    }

    public static class CourseTranDto {

    }

}
