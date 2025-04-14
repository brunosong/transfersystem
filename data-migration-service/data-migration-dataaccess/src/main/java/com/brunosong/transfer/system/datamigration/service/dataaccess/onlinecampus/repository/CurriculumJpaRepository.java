package com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.repository;

import com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.entity.CurriculumEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CurriculumJpaRepository extends JpaRepository<CurriculumEntity, Long> {
    Optional<CurriculumEntity> findByCurriculumId(String curriculumId);
}
