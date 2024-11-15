package com.wjthinkbig.bookclub.cms.metaupload.service.dataaccess.goods.repository;

import com.wjthinkbig.bookclub.cms.metaupload.service.dataaccess.goods.entity.GoodsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoodJpaRepository extends JpaRepository<GoodsEntity,Long> {
}
