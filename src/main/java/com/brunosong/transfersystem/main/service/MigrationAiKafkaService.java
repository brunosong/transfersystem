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

        KafkaSendListDto kafkaSendListDto = new KafkaSendListDto();
        kafkaSendListDto.setChapTranDtoList(chapTranDtoList);

        if(dbProfile.equals("real")) {
            kafkaTemplate.send("ai_chap_topic", kafkaSendListDto );
        } else {
            kafkaTemplate.send("ai_chap_dev_topic", kafkaSendListDto );
        }
    }

    // List를 통째로 보내는 메소드
    public void sendMessageList(String topicName, List<Object> messages) {
        kafkaTemplate.send(topicName, messages);
    }

    // 개별 객체를 보내는 메소드
    public void sendMessage(String topicName, Object message) {
        kafkaTemplate.send(topicName, message);
    }


    @Getter
    @Setter
    public static class KafkaSendListDto {

        private List<ChapTranDto> chapTranDtoList;
        private List<CourseTranDto> courseTranDtoList;

    }

    @Getter
    @Setter
    public static class KafkaSendDto {

        private ChapTranDto chapTranDto;
        private List<CourseTranDto> courseTranDtoList;

    }


}
