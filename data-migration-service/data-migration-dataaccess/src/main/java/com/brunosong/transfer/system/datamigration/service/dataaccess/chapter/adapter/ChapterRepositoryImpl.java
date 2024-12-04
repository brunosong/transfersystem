package com.brunosong.transfer.system.datamigration.service.dataaccess.chapter.adapter;

import com.brunosong.transfer.system.datamigration.service.dataaccess.chapter.mapper.ChapterDataAccessMapper;
import com.brunosong.transfer.system.datamigration.service.dataaccess.chapter.repository.ChapterJpaRepository;
import com.brunosong.transfer.system.datamigration.service.domain.entity.Chapter;
import com.brunosong.transfer.system.datamigration.service.ports.output.repository.ChapterRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
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

    @Override
    public Optional<Chapter> save(Chapter chapter) {
        return Optional.empty();
    }

}
