package com.brunosong.deployment.system.targetagent.service.dataaccess.target.repository;

import com.brunosong.deployment.system.targetagent.service.dataaccess.target.entity.TargetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TargetJpaRepository extends JpaRepository<TargetEntity,Long> {
}
