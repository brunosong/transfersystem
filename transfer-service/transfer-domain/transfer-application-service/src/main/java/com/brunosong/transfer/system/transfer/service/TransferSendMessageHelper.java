package com.brunosong.transfer.system.transfer.service;

import com.brunosong.transfer.system.transfer.service.dto.excution.ExcutionTransferCommand;
import com.brunosong.transfer.system.transfer.service.entity.LearningMaterial;
import com.brunosong.transfer.system.transfer.service.exception.CourseNotFoundException;
import com.brunosong.transfer.system.transfer.service.ports.output.message.publisher.ChapterRequestPublisher;
import com.brunosong.transfer.system.transfer.service.ports.output.message.publisher.CourseRequestPublisher;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.LearningMaterialRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
//@Component
public class TransferSendMessageHelper extends TransferSendHelper {

    private final CourseRequestPublisher courseRequestPublisher;
    private final ChapterRequestPublisher chapterRequestPublisher;

    public TransferSendMessageHelper(LearningMaterialRepository learningMaterialRepository,
                                     CourseRequestPublisher courseRequestPublisher,
                                     ChapterRequestPublisher chapterRequestPublisher) {
        super(learningMaterialRepository);
        this.courseRequestPublisher = courseRequestPublisher;
        this.chapterRequestPublisher = chapterRequestPublisher;
    }

    public void transferAction(ExcutionTransferCommand excutionTransferCommand) {

        LearningMaterial learningMaterial = getLearningMaterial(excutionTransferCommand.getMaterialId());

        if(learningMaterial == null) {
            throw new CourseNotFoundException("Not found LearningMaterial");
        }

    }


    /* AI Service 로 카푸카를 이용해서 데이터를 이관한다. (DB -> Kafka -> DB)  */
//    public void aiKafkaServiceTransferProcess(ExcutionTransferCommand excutionTransferCommand) {
//        migrationServiceSelector.useAiKafkaService();
//        //log.info("Selected MigrationService is {} " , migrationServiceSelector.getCurrentService().getClass().getSimpleName() );
//
////        mainCourseProcess(tranActionReqDto);
////
////        mainChapProcess(tranActionReqDto);
//
//        migrationServiceSelector.clearCurrentService();
//    }


//    public void mainCourseProcess(TranActionDto.TranActionReqDto tranActionReqDto) {
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
