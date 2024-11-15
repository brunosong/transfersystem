package com.brunosong.transfer.system.metaupload.service.dataaccess.metaitem.repository;

import com.brunosong.transfer.system.metaupload.service.dataaccess.metaitem.entity.MetaUploadLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetaUploadLogJpaRepository extends JpaRepository<MetaUploadLogEntity,Integer> {

}
