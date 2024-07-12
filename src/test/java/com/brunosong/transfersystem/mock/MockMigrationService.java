package com.brunosong.transfersystem.mock;

import com.brunosong.transfersystem.main.dto.TranDto;
import com.brunosong.transfersystem.main.service.MigrationService;

import java.util.List;

public class MockMigrationService implements MigrationService {

    @Override
    public void transferCourse(List<TranDto.CourseTranDto> courseTranDtoList, String dbProfile) {

    }

    @Override
    public void transferChap(List<TranDto.ChapTranDto> chapTranDtoList, String dbProfile) {

    }
}
