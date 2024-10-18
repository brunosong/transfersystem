package com.brunosong.transfer.system.dataaccess.meterials.repository;

import com.brunosong.transfer.system.dataaccess.meterials.entity.LearningMaterialViewEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LearningMaterialMongoRepository extends MongoRepository<LearningMaterialViewEntity,String> {
}
