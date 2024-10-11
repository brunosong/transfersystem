package com.brunosong.transfer.system.aiservice.service;

import com.brunosong.transfer.system.aiservice.mapper.ChapMapper;
import com.brunosong.transfer.system.aiservice.dto.chap.AiChapDto;
import com.brunosong.transfer.system.aiservice.dto.chap.AiChapDto.AiChapSaveDto;
import com.brunosong.transfer.system.aiservice.service.chap.AiChapService;
import com.brunosong.transfersystem.config.datasources.DataSourceType;
import com.brunosong.transfersystem.config.datasources.RoutingDataSource;
import com.brunosong.transfersystem.main.dto.TranDto.ChapTranDto;
import com.brunosong.transfersystem.main.dto.TranDto.CourseTranDto;
import com.brunosong.transfersystem.main.service.MigrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/* AiService 기능을 호출하는 클라이언트 Service */
@Service("migrationAiService")
@RequiredArgsConstructor
public class MigrationAiService implements MigrationService {

    private final AiChapService aiChapService;

    private final ChapMapper chapMapper;

    @Override
    public void transferCourse(List<CourseTranDto> courseTranDtoList, String dbProfile) {


    }

    @Transactional("aiServiceJpaTransactionManager")
    @Override
    public void transferChap(List<ChapTranDto> chapTranDtoList, String dbProfile) {

        List<AiChapDto.AiChapSaveDto> aiChapSaveDtoList = chapTranDtoList.stream().map(chapMapper::toChapSaveDto)
                .collect(Collectors.toList());

        saveChapProcess(dbProfile, () -> {
            for (AiChapSaveDto aiChapSaveDto : aiChapSaveDtoList) {
                aiChapService.save(aiChapSaveDto);
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
