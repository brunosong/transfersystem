package com.brunosong.transfer.system.transfer.dataaccess.course.repository;

import com.brunosong.transfer.system.transfer.dataaccess.chapter.entity.ChapterEntity;
import com.brunosong.transfer.system.transfer.dataaccess.course.entity.CourseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseJpaRepository extends JpaRepository<CourseEntity,Long> {
    ChapterEntity findByCourseSeq(Long courseSeq);
}
