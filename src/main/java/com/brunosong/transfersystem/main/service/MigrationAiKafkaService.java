package com.brunosong.transfersystem.main.service;

import com.brunosong.transfersystem.main.dto.TranDto;
import com.brunosong.transfersystem.main.dto.TranDto.ChapTranDto;
import com.brunosong.transfersystem.main.dto.TranDto.CourseTranDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("migrationAiKafkaService")
@RequiredArgsConstructor
public class MigrationAiKafkaService implements MigrationService {

    private final KafkaTemplate<Object,Object> kafkaTemplate;

    @Override
    public void transferCourse(List<CourseTranDto> courseTranDtoList, String dbProfile) {

    }

    @Override
    public void transferChap(List<ChapTranDto> chapTranDtoList, String dbProfile) {

        KafkaSendDto kafkaSendDto = new KafkaSendDto();
        kafkaSendDto.setChapTranDtoList(chapTranDtoList);

        if(dbProfile.equals("real")) {
            kafkaTemplate.send("chap_topic", chapTranDtoList );
        } else {
            kafkaTemplate.send("chap_dev_topic", chapTranDtoList );
        }


    }


    @Getter
    @Setter
    public static class KafkaSendDto {

        private List<ChapTranDto> chapTranDtoList;
        private List<CourseTranDto> courseTranDtoList;

    }


}
