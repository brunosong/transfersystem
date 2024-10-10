package com.brunosong.transfer.system.main.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/* 디폴트로 사용되는 클래스로 어떤 값들이 이관이 되는지 알수 있도록 출력을 해주는 클래스 */
@Primary
@Service("migrationDefaultService")
@RequiredArgsConstructor
public class MigrationDefaultService implements MigrationService {
//
//    @Override
//    public void transferCourse(List<TranDto.CourseTranDto> courseTranDtoList, String dbProfile) {
//
//    }
//
//    @Override
//    public void transferChap(List<TranDto.ChapTranDto> chapTranDtoList, String dbProfile) {
//
//    }
}
