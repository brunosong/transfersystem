package com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.repository;

import com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.entity.SubjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubjectJpaRepository extends JpaRepository<SubjectEntity, Long> {
    Optional<SubjectEntity> findBySubjectId(String subjectId);
}
