package com.brunosong.transfer.system.transfer.service;

import com.brunosong.transfer.system.transfer.service.dto.excution.ExcutionTransferCommand;
import com.brunosong.transfer.system.transfer.service.entity.Course;
import com.brunosong.transfer.system.transfer.service.exception.CourseNotFoundException;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.ChapterRepository;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.CourseRepository;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.SaveTargetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class TransferSendRepositoryHelper extends TransferSendHelper {

    private final SaveTargetRepository saveTargetRepository;
    public TransferSendRepositoryHelper(CourseRepository courseRepository, ChapterRepository chapterRepository, SaveTargetRepository saveTargetRepository) {
        super(courseRepository, chapterRepository);
        this.saveTargetRepository = saveTargetRepository;
    }

    @Override
    public void courseTransferProcess(ExcutionTransferCommand excutionTransferCommand) {
        //course 정보를 가져오고
        List<Course> courseList = courseRepository.findAll();

        if(courseList.isEmpty()) {
            throw new CourseNotFoundException("Empty Course");
        }

        saveTargetRepository.saveCourses(courseList);

    }

    @Override
    void chapterTransferProcess(ExcutionTransferCommand excutionTransferCommand) {

    }

    /* AI Service 로 데이터를 이관한다. (DB -> DB)  */
//    public void aiServiceTransferProcess(ExcutionTransferCommand excutionTransferCommand) {
//        migrationServiceSelector.useAiService();
//        log.info("Selected MigrationService is {} " , migrationServiceSelector.getCurrentService().getClass().getSimpleName() );
//
//        mainChapProcess(excutionTransferCommand);
//
//        migrationServiceSelector.clearCurrentService();
//    }

//    public void mainCourseProcess(ExcutionTransferCommand excutionTransferCommand) {
//
//        /* Course 로직 */
//        List<MainCourse> mainCourses = mainCourseRepository.findAll();
//
//        if(mainCourses.isEmpty()) {
//            throw new TranCustomException("Empty MainCourse");
//        }
//
//        List<TranDto.CourseTranDto> courseTranDtoList = mainCourses.stream().map(TranDto.CourseTranDto::fromEntity)
//                .collect(Collectors.toList());
//
//        migrationServiceSelector.getCurrentService().transferCourse(courseTranDtoList, tranActionReqDto.getDbProfile());
//
//    }
//
//    public void mainChapProcess(ExcutionTransferCommand excutionTransferCommand) {
//
//        /* Chapter */
//        List<Chapter> mainChaps = chapterRepository.findAll();
//
//        if(mainChaps.isEmpty()) {
//            throw new TranCustomException("Empty MainChap");
//        }
//
//        List<Chapter> chapTranDtoList = mainChaps.stream().map(TranDto.ChapTranDto::fromEntity)
//                .collect(Collectors.toList());
//
//        migrationServiceSelector.getCurrentService().transferChap(chapTranDtoList, tranActionReqDto.getDbProfile());
//    }
}
