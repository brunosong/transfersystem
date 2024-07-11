package com.brunosong.transfersystem.main.service;

import com.brunosong.transfersystem.main.dto.TranDto;
import com.brunosong.transfersystem.main.dto.TranDto.ChapTranDto;
import com.brunosong.transfersystem.main.dto.TranDto.CourseTranDto;

import java.util.List;

public interface TranActionService {

    void insertCourse(List<CourseTranDto> courseTranDtoList);

    void insertChap(List<ChapTranDto> chapTranDtoList);


}
