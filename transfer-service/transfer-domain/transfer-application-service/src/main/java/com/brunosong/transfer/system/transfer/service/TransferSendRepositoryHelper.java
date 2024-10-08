package com.brunosong.transfer.system.transfer.service;

import com.brunosong.transfer.system.transfer.service.dto.excution.ExcutionTransferCommand;
import com.brunosong.transfer.system.transfer.service.entity.Chapter;
import com.brunosong.transfer.system.transfer.service.entity.Course;
import com.brunosong.transfer.system.transfer.service.exception.ChapterNotFoundException;
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
        List<Course> courseList = courseRepository.findByUpSeq(excutionTransferCommand.getUpSeq());

        if(courseList.isEmpty()) {
            throw new CourseNotFoundException("Empty Course");
        }

        saveTargetRepository.saveCourses(courseList);
    }

    @Override
    void chapterTransferProcess(ExcutionTransferCommand excutionTransferCommand) {

        List<Chapter> chapterList = chapterRepository.findByCourseSeq(excutionTransferCommand.getUpSeq());

        if(chapterList.isEmpty()) {
            throw new ChapterNotFoundException("Empty Chapter");
        }

        saveTargetRepository.saveChapters(chapterList);
    }

}
