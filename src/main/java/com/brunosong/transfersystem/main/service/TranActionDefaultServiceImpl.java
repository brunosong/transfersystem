package com.brunosong.transfersystem.main.service;

import com.brunosong.transfersystem.main.dto.TranDto;
import com.brunosong.transfersystem.main.dto.TranDto.ChapTranDto;
import com.brunosong.transfersystem.main.dto.TranDto.CourseTranDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

/* 디폴트로 사용되는 클래스로 어떤 값들이 이관이 되는지 알수 있도록 출력을 해주는 클래스 */
@Primary
@Service("tranActionDefaultService")
@RequiredArgsConstructor
public class TranActionDefaultServiceImpl implements TranActionService {

    @Override
    public void insertChap(List<ChapTranDto> chapTranDtoList) {

    }

    @Override
    public void insertCourse(List<CourseTranDto> courseTranDtoList) {

    }

}
