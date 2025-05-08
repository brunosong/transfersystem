package com.brunosong.transfer.system.transfer.service.cache.transfer.repository;

import com.brunosong.transfer.system.transfer.service.cache.transfer.entity.TransferCacheEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferLogRedisRepository extends CrudRepository<TransferCacheEntity, String> {
}
