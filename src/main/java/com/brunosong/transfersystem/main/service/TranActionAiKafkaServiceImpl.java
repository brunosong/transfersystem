package com.brunosong.transfersystem.main.service;

import com.brunosong.transfersystem.main.dto.TranDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("tranActionAiKafkaService")
@RequiredArgsConstructor
public class TranActionAiKafkaServiceImpl implements TranActionService {

    @Override
    public void insertChap(List<TranDto.ChapTranDto> chapTranDtoList) {

    }

    @Override
    public void insertCourse(List<TranDto.CourseTranDto> courseTranDtoList) {

    }
}
