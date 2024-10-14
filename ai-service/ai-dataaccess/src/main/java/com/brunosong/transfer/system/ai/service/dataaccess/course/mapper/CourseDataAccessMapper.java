package com.brunosong.transfer.system.ai.service.dataaccess.course.mapper;

import com.brunosong.transfer.system.ai.service.dataaccess.chapter.mapper.ChapterDataAccessMapper;
import com.brunosong.transfer.system.ai.service.dataaccess.course.entity.CourseEntity;
import com.brunosong.transfer.system.transfer.service.entity.Course;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CourseDataAccessMapper {

    private final ChapterDataAccessMapper chapterDataAccessMapper;

    public Course courseEntityToCourse(CourseEntity courseEntity) {
        return Course.builder()
                .courseSeq(courseEntity.getCourseSeq())
                .courseName(courseEntity.getCourseName())
                .chapterList(courseEntity.getChapterList().stream()
                        .map(chapterDataAccessMapper::chapterEntityToChapter)
                        .collect(Collectors.toList()))
                .build();
    }

}
