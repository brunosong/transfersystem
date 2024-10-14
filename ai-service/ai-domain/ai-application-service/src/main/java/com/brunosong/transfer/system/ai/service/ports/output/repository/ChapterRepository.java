package com.brunosong.transfer.system.ai.service.ports.output.repository;

import com.brunosong.transfer.system.ai.service.domain.entity.Chapter;

import java.util.List;

public interface ChapterRepository {
    List<Chapter> findByCourseSeq(Long courseSeq);
}
