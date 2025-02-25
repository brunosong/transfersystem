package com.brunosong.transfer.system.metaupload.service.dataaccess.goods.repository;

import com.brunosong.transfer.system.metaupload.service.dataaccess.goods.entity.GoodsMetaMasterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GoodsMetaMasterJpaRepository extends JpaRepository<GoodsMetaMasterEntity, Long> {

    @Query("select m from GoodsMetaMasterEntity m where m.metaCode in :metaCodeList")
    List<GoodsMetaMasterEntity> findByMetaCodeList(List<String> metaCodeList);

    Optional<GoodsMetaMasterEntity> findByMetaCode(String metaCode);

}
