package com.brunosong.transfersystem.main.service;

import com.brunosong.transfersystem.main.dto.TranDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("migrationAiKafkaService")
@RequiredArgsConstructor
public class MigrationAiKafkaService implements MigrationService {

    @Override
    public void transferCourse(List<TranDto.CourseTranDto> courseTranDtoList) {

    }

    @Override
    public void transferChap(List<TranDto.ChapTranDto> chapTranDtoList) {

    }
}
