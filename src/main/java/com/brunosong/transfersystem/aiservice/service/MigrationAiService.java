package com.brunosong.transfersystem.aiservice.service;

import com.brunosong.transfersystem.main.dto.TranDto;
import com.brunosong.transfersystem.main.service.MigrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("migrationAiService")
@RequiredArgsConstructor
public class MigrationAiService implements MigrationService {

    @Override
    public void transferCourse(List<TranDto.CourseTranDto> courseTranDtoList) {

    }

    @Override
    public void transferChap(List<TranDto.ChapTranDto> chapTranDtoList) {

    }

}
