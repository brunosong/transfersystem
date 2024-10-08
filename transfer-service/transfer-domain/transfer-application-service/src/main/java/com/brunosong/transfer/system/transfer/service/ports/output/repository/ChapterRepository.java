package com.brunosong.transfer.system.transfer.service.ports.output.repository;

import com.brunosong.transfer.system.transfer.service.entity.Chapter;
import com.brunosong.transfer.system.transfer.service.entity.Course;

import java.util.List;

public interface ChapterRepository {
    List<Chapter> findByCourseSeq(Long courseSeq);
}
