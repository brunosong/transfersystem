package com.brunosong.transfer.system.main.service;


import com.brunosong.transfer.system.main.domain.chap.MainChap;
import com.brunosong.transfer.system.main.domain.course.MainCourse;
import com.brunosong.transfer.system.main.dto.TranActionDto;
import com.brunosong.transfer.system.main.dto.TranDto;
import com.brunosong.transfer.system.main.repository.MainChapRepository;
import com.brunosong.transfer.system.main.repository.MainCourseRepository;
import com.brunosong.transfer.system.main.service.exception.TranCustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TranService {
    private final MainChapRepository mainChapRepository;

    private final MainCourseRepository mainCourseRepository;

    private final MigrationServiceSelector migrationServiceSelector;


    /* AI Service 로 데이터를 이관한다. (DB -> DB)  */
    public void aiServiceTransferProcess(TranActionDto.TranActionReqDto tranActionReqDto) {
        migrationServiceSelector.useAiService();
        log.info("Selected MigrationService is {} " , migrationServiceSelector.getCurrentService().getClass().getSimpleName() );

        mainChapProcess(tranActionReqDto);

        migrationServiceSelector.clearCurrentService();
    }


    /* AI Service 로 카푸카를 이용해서 데이터를 이관한다. (DB -> Kafka -> DB)  */
    public void aiKafkaServiceTransferProcess(TranActionDto.TranActionReqDto tranActionReqDto) {
        migrationServiceSelector.useAiKafkaService();
        log.info("Selected MigrationService is {} " , migrationServiceSelector.getCurrentService().getClass().getSimpleName() );

        mainCourseProcess(tranActionReqDto);

        mainChapProcess(tranActionReqDto);

        migrationServiceSelector.clearCurrentService();
    }


    public void mainCourseProcess(TranActionDto.TranActionReqDto tranActionReqDto) {

        /* Course 로직 */
        List<MainCourse> mainCourses = mainCourseRepository.findAll();

        if(mainCourses.isEmpty()) {
            throw new TranCustomException("Empty MainCourse");
        }

        List<TranDto.CourseTranDto> courseTranDtoList = mainCourses.stream().map(TranDto.CourseTranDto::fromEntity)
                .collect(Collectors.toList());

        migrationServiceSelector.getCurrentService().transferCourse(courseTranDtoList, tranActionReqDto.getDbProfile());

    }

    public void mainChapProcess(TranActionDto.TranActionReqDto tranActionReqDto) {

        /* Chapter */
        List<MainChap> mainChaps = mainChapRepository.findAll();

        if(mainChaps.isEmpty()) {
            throw new TranCustomException("Empty MainChap");
        }

        List<TranDto.ChapTranDto> chapTranDtoList = mainChaps.stream().map(TranDto.ChapTranDto::fromEntity)
                .collect(Collectors.toList());

        migrationServiceSelector.getCurrentService().transferChap(chapTranDtoList, tranActionReqDto.getDbProfile());
    }


}
