package com.brunosong.transfer.system.metaupload.service.dataaccess.goods.repository;

import com.brunosong.transfer.system.metaupload.service.dataaccess.goods.entity.GoodsMetaItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoodsMetaItemJpaRepository extends JpaRepository<GoodsMetaItemEntity, Long> {
    
}
