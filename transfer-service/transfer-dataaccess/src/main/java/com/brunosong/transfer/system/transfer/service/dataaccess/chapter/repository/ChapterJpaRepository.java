package com.brunosong.transfer.system.transfer.service.dataaccess.chapter.repository;

import com.brunosong.transfer.system.transfer.service.dataaccess.chapter.entity.ChapterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChapterJpaRepository extends JpaRepository<ChapterEntity,Long> {
    List<ChapterEntity> findByCourseCourseSeq(Long courseSeq);
}
