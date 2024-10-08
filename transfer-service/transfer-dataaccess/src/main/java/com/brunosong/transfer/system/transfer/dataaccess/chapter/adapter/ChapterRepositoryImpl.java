package com.brunosong.transfer.system.transfer.dataaccess.chapter.adapter;

import com.brunosong.transfer.system.transfer.dataaccess.chapter.mapper.ChapterDataAccessMapper;
import com.brunosong.transfer.system.transfer.dataaccess.chapter.repository.ChapterJpaRepository;
import com.brunosong.transfer.system.transfer.dataaccess.course.entity.CourseEntity;
import com.brunosong.transfer.system.transfer.service.entity.Chapter;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.ChapterRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ChapterRepositoryImpl implements ChapterRepository {

    private final ChapterJpaRepository chapterJpaRepository;
    private final ChapterDataAccessMapper dataAccessMapper;

    public ChapterRepositoryImpl(ChapterJpaRepository chapterJpaRepository,
                                 ChapterDataAccessMapper dataAccessMapper) {
        this.chapterJpaRepository = chapterJpaRepository;
        this.dataAccessMapper = dataAccessMapper;
    }

    @Override
    public List<Chapter> findByCourseSeq(Long courseSeq) {
        return chapterJpaRepository.findByCourseCourseSeq(courseSeq).stream()
                                                                .map(dataAccessMapper::chapterEntityToChapter)
                                                                .collect(Collectors.toList());
    }

}
