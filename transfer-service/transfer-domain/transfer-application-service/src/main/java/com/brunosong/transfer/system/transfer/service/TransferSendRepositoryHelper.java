package com.brunosong.transfer.system.transfer.service;

import com.brunosong.transfer.system.transfer.service.dto.excution.ExcutionTransferCommand;
import com.brunosong.transfer.system.transfer.service.entity.Chapter;
import com.brunosong.transfer.system.transfer.service.entity.Course;
import com.brunosong.transfer.system.transfer.service.entity.LearningMaterial;
import com.brunosong.transfer.system.transfer.service.exception.ChapterNotFoundException;
import com.brunosong.transfer.system.transfer.service.exception.CourseNotFoundException;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.ChapterRepository;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.CourseRepository;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.LearningMaterialRepository;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.SaveTargetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class TransferSendRepositoryHelper extends TransferSendHelper {

    private final SaveTargetRepository saveTargetRepository;

    public TransferSendRepositoryHelper(LearningMaterialRepository learningMaterialRepository,
                                        SaveTargetRepository saveTargetRepository) {
        super(learningMaterialRepository);
        this.saveTargetRepository = saveTargetRepository;
    }

    @Override
    public void courseTransferProcess(ExcutionTransferCommand excutionTransferCommand) {

        List<LearningMaterial> learningMaterialList = getLearningMaterialList(excutionTransferCommand.getMaterialId());

        if(learningMaterialList.isEmpty()) {
            throw new CourseNotFoundException("Empty Course");
        }

        // domain.findCourse()
        // saveTargetRepository.saveCourse(course.get());
    }

    @Override
    void chapterTransferProcess(ExcutionTransferCommand excutionTransferCommand) {

//        List<Chapter> chapterList = chapterRepository.findByCourseSeq(excutionTransferCommand.getCourseSeq());
//
//        if(chapterList.isEmpty()) {
//            throw new ChapterNotFoundException("Empty Chapter");
//        }

        //saveTargetRepository.saveChapters(chapterList);
    }

}
