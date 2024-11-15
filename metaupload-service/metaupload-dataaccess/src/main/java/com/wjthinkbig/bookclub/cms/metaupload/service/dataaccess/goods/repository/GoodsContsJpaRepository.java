package com.wjthinkbig.bookclub.cms.metaupload.service.dataaccess.goods.repository;

import com.wjthinkbig.bookclub.cms.metaupload.service.dataaccess.goods.entity.GoodsContsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GoodsContsJpaRepository extends JpaRepository<GoodsContsEntity, Long> {

    @Query("select c from GoodsContsEntity c where c.goodsSeq = :goodsSeq and c.contentsSeq = :contentsSeq and c.goodsMetaMaster.masterSeq = :masterSeq")
    Optional<GoodsContsEntity> findByGoodsSeqAndMasterSeq( @Param("goodsSeq") Long goodsSeq, @Param("contentsSeq") Long contentsSeq ,
                                                           @Param("masterSeq") Long masterSeq );

    @Query("select distinct c from GoodsContsEntity c LEFT JOIN FETCH c.goodsContsMetaEntityList where c.goodsSeq = :goodsSeq and c.goodsMetaMaster.masterSeq = :masterSeq")
    List<GoodsContsEntity> findByGoodsSeqAndMasterSeq(@Param("goodsSeq") Long goodsSeq, @Param("masterSeq") Long masterSeq );

    @Query("select c from GoodsContsEntity c where c.goodsMetaMaster.masterSeq = :masterSeq and c.goodsSeq in (:goodsSeqList)")
    List<GoodsContsEntity> findByGoodsSeqAndMasterSeq(@Param("masterSeq") Long masterSeq, @Param("goodsSeqList") List<Long> goodsSeqList );

    @Modifying(clearAutomatically = true)
    @Query("update GoodsContsMetaEntity g set g.itemSeq = :itemSeq " +
            "where g.gcSeq = :gcSeq and g.goodsMetaMaster.masterSeq = :masterSeq")
    int updateContsMetaItemSeq(@Param("gcSeq") Long gcSeq, @Param("masterSeq") Long masterSeq, @Param("itemSeq") Long itemSeq);


    @Modifying(clearAutomatically = true)
    @Query("update GoodsContsMetaEntity g set g.metaValue = :metaValue " +
            "where g.gcSeq = :gcSeq and g.goodsMetaMaster.masterSeq = :masterSeq")
    int updateContsMetaMetaValue(@Param("gcSeq") Long gcSeq, @Param("masterSeq") Long masterSeq, @Param("metaValue") String metaValue);

}
