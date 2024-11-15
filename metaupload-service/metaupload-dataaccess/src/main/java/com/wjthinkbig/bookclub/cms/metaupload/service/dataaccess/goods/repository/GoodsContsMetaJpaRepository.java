package com.wjthinkbig.bookclub.cms.metaupload.service.dataaccess.goods.repository;

import com.wjthinkbig.bookclub.cms.metaupload.service.dataaccess.goods.entity.GoodsContsMetaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GoodsContsMetaJpaRepository extends JpaRepository<GoodsContsMetaEntity, Long> {

    @Query("select c from GoodsContsMetaEntity c where c.gcSeq = :gcSeq and c.goodsMetaMaster.masterSeq = :masterSeq" )
    Optional<GoodsContsMetaEntity> findByGcSeqAndGoodsMetaMaster( @Param("gcSeq") Long gcSeq, @Param("masterSeq") Long masterSeq);

}
