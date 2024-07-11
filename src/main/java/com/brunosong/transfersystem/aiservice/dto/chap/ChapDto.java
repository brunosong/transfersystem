package com.brunosong.transfersystem.aiservice.dto.chap;

import com.brunosong.transfersystem.aiservice.domain.Chap;
import lombok.Getter;
import lombok.Setter;

public class ChapDto {

    @Getter
    @Setter
    public static class ChapSaveDto {

        Long chapSeq;
        String chapTitle;
        String chapType;

        public Chap toDomain() {
            return Chap.builder()
                .chapSeq(this.getChapSeq())
                .chapTitle(this.getChapTitle())
                .chapType(Chap.ChapTypeEnum.valueOf(this.getChapType()))
                .build();
        }

    }

}
