package com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.repository;

import com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.entity.SemesterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SemesterJpaRepository extends JpaRepository<SemesterEntity, Long> {
    Optional<SemesterEntity> findBySemesterId(String semesterId);
}
