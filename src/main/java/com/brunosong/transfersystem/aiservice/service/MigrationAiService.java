package com.brunosong.transfersystem.aiservice.service;

import com.brunosong.transfersystem.aiservice.dto.chap.ChapDto.ChapSaveDto;
import com.brunosong.transfersystem.aiservice.mapper.ChapMapper;
import com.brunosong.transfersystem.aiservice.service.chap.ChapService;
import com.brunosong.transfersystem.config.datasources.DataSourceType;
import com.brunosong.transfersystem.config.datasources.RoutingDataSource;
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

        saveChapProcess(dbProfile, () -> {
            for (ChapSaveDto chapSaveDto : chapSaveDtoList) {
                chapService.save(chapSaveDto);
            }
            return null;
        });

    }

    public void saveChapProcess(String dbProfile ,Supplier<?> process) {
        if(dbProfile.equals("real")) {
            RoutingDataSource.setDataSourceType(DataSourceType.AISERVICE_REAL);
        } else {
            RoutingDataSource.setDataSourceType(DataSourceType.AISERVICE_DEV);
        }

        process.get();

        RoutingDataSource.clearDataSourceType();
    }

}
