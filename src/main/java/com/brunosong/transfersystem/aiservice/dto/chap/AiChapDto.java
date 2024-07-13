package com.brunosong.transfersystem.aiservice.dto.chap;

import com.brunosong.transfersystem.aiservice.domain.AiChap;
import lombok.Getter;
import lombok.Setter;

public class AiChapDto {

    @Getter
    @Setter
    public static class AiChapSaveDto {

        Long aiChapSeq;
        String aiChapTitle;
        String aiChapType;

        public AiChap toDomain() {
            return AiChap.builder()
                .aiChapSeq(this.getAiChapSeq())
                .aiChapTitle(this.getAiChapTitle())
                .aiChapType(AiChap.AiChapTypeEnum.valueOf(this.getAiChapType()))
                .build();
        }

    }

}
