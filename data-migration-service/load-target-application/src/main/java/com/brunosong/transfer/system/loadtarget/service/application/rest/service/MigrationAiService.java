package com.brunosong.transfer.system.datamigration.service.application.rest.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/* AiService 기능을 호출하는 클라이언트 Service */
@Service("migrationAiService")
@RequiredArgsConstructor
public class MigrationAiService // implements MigrationService {
{

//    private final AiChapService aiChapService;
//
//    private final ChapMapper chapMapper;
//
//    @Override
//    public void transferCourse(List<CourseTranDto> courseTranDtoList, String dbProfile) {
//
//
//    }
//
//    @Transactional("aiServiceJpaTransactionManager")
//    @Override
//    public void transferChap(List<ChapTranDto> chapTranDtoList, String dbProfile) {
//
//        List<AiChapDto.AiChapSaveDto> aiChapSaveDtoList = chapTranDtoList.stream().map(chapMapper::toChapSaveDto)
//                .collect(Collectors.toList());
//
//        saveChapProcess(dbProfile, () -> {
//            for (AiChapSaveDto aiChapSaveDto : aiChapSaveDtoList) {
//                aiChapService.save(aiChapSaveDto);
//            }
//            return null;
//        });
//
//    }

//
//
//    public void saveChapProcess(String dbProfile ,Supplier<?> process) {
//        if(dbProfile.equals("real")) {
//            RoutingDataSource.setDataSourceType(DataSourceType.AISERVICE_REAL);
//        } else {
//            RoutingDataSource.setDataSourceType(DataSourceType.AISERVICE_DEV);
//        }
//
//        process.get();
//
//        RoutingDataSource.clearDataSourceType();
//    }

}
