package com.brunosong.transfer.system.transfer.service.dataaccess.source.repository;

import com.brunosong.transfer.system.transfer.service.dataaccess.source.entity.SourceConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SourceConfigJpaRepository extends JpaRepository<SourceConfigEntity, Long> {
}
