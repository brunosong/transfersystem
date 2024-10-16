package com.brunosong.transfer.system.dataaccess.meterials.repository;

import com.brunosong.transfer.system.dataaccess.meterials.entity.LearningMaterialMongoEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LearningMaterialMongoRepository extends MongoRepository<LearningMaterialMongoEntity,String> {
}
