package com.brunosong.transfer.system.main.service;

import com.brunosong.transfer.system.main.dto.TranDto;

import java.util.List;

public interface MigrationService {

    void transferCourse(List<TranDto.CourseTranDto> courseTranDtoList, String dbProfile);

    void transferChap(List<TranDto.ChapTranDto> chapTranDtoList, String dbProfile);


}
