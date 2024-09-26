package com.brunosong.transfer.system.transfer.dataaccess.chapter.repository;

import com.brunosong.transfer.system.transfer.dataaccess.chapter.entity.ChapterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChapterJpaRepository extends JpaRepository<ChapterEntity,Long> {
}
