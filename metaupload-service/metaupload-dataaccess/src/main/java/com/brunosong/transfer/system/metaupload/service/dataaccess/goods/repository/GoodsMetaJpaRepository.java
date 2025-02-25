package com.brunosong.transfer.system.metaupload.service.dataaccess.goods.repository;

import com.brunosong.transfer.system.metaupload.service.dataaccess.goods.entity.GoodsMetaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GoodsMetaJpaRepository extends JpaRepository<GoodsMetaEntity, Long> {

    Optional<GoodsMetaEntity> findByGoodsSeqAndMasterSeq(@Param("goodsSeq") Long goodsSeq, @Param("masterSeq") Long masterSeq);

    @Modifying(clearAutomatically = true)
    @Query("update GoodsMetaEntity g set g.itemSeq = :itemSeq " +
            "where g.goodsSeq = :goodsSeq and g.masterSeq = :masterSeq")
    int updateItemSeq(@Param("goodsSeq") Long goodsSeq, @Param("masterSeq") Long masterSeq,
                      @Param("itemSeq") Long itemSeq);


    @Modifying(clearAutomatically = true)
    @Query("update GoodsMetaEntity g set g.metaValue = :metaValue " +
            "where g.goodsSeq = :goodsSeq and g.masterSeq = :masterSeq")
    int updateMetaValue(@Param("goodsSeq") Long goodsSeq, @Param("masterSeq") Long masterSeq,
                        @Param("metaValue") String metaValue);

}
