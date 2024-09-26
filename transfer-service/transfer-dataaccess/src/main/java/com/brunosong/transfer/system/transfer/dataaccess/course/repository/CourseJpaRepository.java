package com.brunosong.transfer.system.transfer.dataaccess.course.repository;

import com.brunosong.transfer.system.transfer.dataaccess.course.entity.CourseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseJpaRepository extends JpaRepository<CourseEntity,Long> {

}
