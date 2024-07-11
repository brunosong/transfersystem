package com.brunosong.transfersystem.aiservice.service;

import com.brunosong.transfersystem.main.dto.TranDto;
import com.brunosong.transfersystem.main.service.TranActionService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Primary
@Service("tranActionAiService")
@RequiredArgsConstructor
public class TranActionAiServiceImpl implements TranActionService {

    @Override
    public void insertChap(List<TranDto.ChapTranDto> chapTranDtoList) {

    }

    @Override
    public void insertCourse(List<TranDto.CourseTranDto> courseTranDtoList) {

    }
}
