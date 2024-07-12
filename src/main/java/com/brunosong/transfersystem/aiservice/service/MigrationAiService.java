package com.brunosong.transfersystem.aiservice.service;

import com.brunosong.transfersystem.aiservice.dto.chap.ChapDto.ChapSaveDto;
import com.brunosong.transfersystem.aiservice.mapper.ChapMapper;
import com.brunosong.transfersystem.aiservice.service.chap.ChapService;
import com.brunosong.transfersystem.config.annotation.UseAiServiceDevDataSource;
import com.brunosong.transfersystem.config.annotation.UseAiServiceRealDataSource;
import com.brunosong.transfersystem.main.dto.TranDto.ChapTranDto;
import com.brunosong.transfersystem.main.dto.TranDto.CourseTranDto;
import com.brunosong.transfersystem.main.service.MigrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service("migrationAiService")
@RequiredArgsConstructor
public class MigrationAiService implements MigrationService {

    private final ChapService chapService;

    private final ChapMapper chapMapper;

    @Override
    public void transferCourse(List<CourseTranDto> courseTranDtoList, String dbProfile) {


    }

    @Override
    public void transferChap(List<ChapTranDto> chapTranDtoList, String dbProfile) {

        List<ChapSaveDto> chapSaveDtoList = chapTranDtoList.stream().map(chapMapper::toChapSaveDto)
                .collect(Collectors.toList());

        Supplier<?> saveProcess = () -> {
            for (ChapSaveDto chapSaveDto : chapSaveDtoList) {
                chapService.save(chapSaveDto);
            }
            return null;
        };

        if(dbProfile.equals("real")) {
            aiRealDbSave(saveProcess);
        } else {
            aiDevDbSave(saveProcess);
        }
    }

    /* 템플릿 메소드 패턴을 사용할지 전략패턴을 사용할지 고민하다 템플릿 메소드 적용 */
    @UseAiServiceRealDataSource
    private void aiRealDbSave(Supplier<?> supplier) {
        supplier.get();
    }

    @UseAiServiceDevDataSource
    private void aiDevDbSave(Supplier<?> supplier) {
        supplier.get();
    }

}
