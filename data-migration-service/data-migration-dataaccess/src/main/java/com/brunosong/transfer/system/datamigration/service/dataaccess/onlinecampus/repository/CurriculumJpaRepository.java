package com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.repository;

import com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.entity.Curriculum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurriculumJpaRepository extends JpaRepository<Curriculum, Long> {
}
