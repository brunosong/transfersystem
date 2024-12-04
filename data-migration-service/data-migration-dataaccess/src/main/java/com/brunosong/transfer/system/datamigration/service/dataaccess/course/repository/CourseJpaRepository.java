package com.brunosong.transfer.system.datamigration.service.dataaccess.course.repository;

import com.brunosong.transfer.system.datamigration.service.dataaccess.course.entity.CourseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseJpaRepository extends JpaRepository<CourseEntity,Long> {}
