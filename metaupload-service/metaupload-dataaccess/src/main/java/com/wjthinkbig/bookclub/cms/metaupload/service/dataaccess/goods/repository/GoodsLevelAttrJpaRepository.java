package com.wjthinkbig.bookclub.cms.metaupload.service.dataaccess.goods.repository;

import com.wjthinkbig.bookclub.cms.metaupload.service.dataaccess.goods.entity.GoodsLevelAttrEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoodsLevelAttrJpaRepository extends JpaRepository<GoodsLevelAttrEntity,Long> {
}
