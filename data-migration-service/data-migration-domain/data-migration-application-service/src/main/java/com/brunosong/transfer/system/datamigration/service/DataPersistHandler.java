package com.brunosong.transfer.system.datamigration.service;

import com.brunosong.transfer.system.datamigration.service.domain.entity.Chapter;
import com.brunosong.transfer.system.datamigration.service.domain.entity.Course;
import com.brunosong.transfer.system.datamigration.service.ports.output.repository.ChapterRepository;
import com.brunosong.transfer.system.datamigration.service.ports.output.repository.CourseRepository;
import org.springframework.stereotype.Component;

@Component
public class DataPersistHandler {

    private ChapterRepository chapterRepository;
    private CourseRepository courseRepository;
    
    public void convert() {}

    public void save() {
        Chapter build = Chapter.builder().build();
        chapterRepository.save(build);

        Course build1 = Course.builder().build();
        courseRepository.save(build1);
    }
}
