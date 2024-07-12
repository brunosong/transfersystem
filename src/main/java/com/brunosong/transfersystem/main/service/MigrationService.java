package com.brunosong.transfersystem.main.service;

import com.brunosong.transfersystem.main.dto.TranDto.ChapTranDto;
import com.brunosong.transfersystem.main.dto.TranDto.CourseTranDto;

import java.util.List;

public interface MigrationService {

    void transferCourse(List<CourseTranDto> courseTranDtoList);

    void transferChap(List<ChapTranDto> chapTranDtoList);


}
