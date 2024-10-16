package com.brunosong.transfer.system.ai.service.ports.output.repository;

import com.brunosong.transfer.system.ai.service.domain.entity.Chapter;

import java.util.List;
import java.util.Optional;

public interface ChapterRepository {
    List<Chapter> findByCourseSeq(Long courseSeq);

    Optional<Chapter> save(Chapter chapter);
}
