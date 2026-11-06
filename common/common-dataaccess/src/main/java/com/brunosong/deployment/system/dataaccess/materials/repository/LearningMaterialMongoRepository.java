package com.brunosong.deployment.system.dataaccess.materials.repository;

import com.brunosong.deployment.system.dataaccess.materials.entity.LearningMaterialViewEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LearningMaterialMongoRepository extends MongoRepository<LearningMaterialViewEntity,String> {
}
