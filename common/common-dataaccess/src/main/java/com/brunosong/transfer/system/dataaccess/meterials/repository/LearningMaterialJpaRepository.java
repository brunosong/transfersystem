package com.brunosong.transfer.system.dataaccess.meterials.repository;

import com.brunosong.transfer.system.dataaccess.meterials.entity.LearningMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LearningMaterialJpaRepository extends JpaRepository<LearningMaterial,Long> {
}
