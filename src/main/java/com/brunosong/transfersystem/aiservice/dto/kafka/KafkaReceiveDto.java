package com.brunosong.transfersystem.aiservice.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

public class KafkaReceiveDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KafkaChapReceiveDto {

        private String dbProfile;
        private KafkaChapDto chapDto;
        private List<KafkaChapDto> chapTranDtoList;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KafkaChapDto {
        private Long chapSeq;
        private String chapTitle;
        private String chapType;
    }


}
