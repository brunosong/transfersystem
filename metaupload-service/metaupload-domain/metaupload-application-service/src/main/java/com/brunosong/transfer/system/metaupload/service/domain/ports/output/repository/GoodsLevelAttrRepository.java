package com.brunosong.transfer.system.metaupload.service.domain.ports.output.repository;

import com.brunosong.transfer.system.metaupload.service.entity.GoodsLevelAttr;

import java.util.List;
import java.util.Optional;

public interface GoodsLevelAttrRepository {

    Optional<List<GoodsLevelAttr>> findGoodsMetaLevelAttr(Long goodsSeq, int level);
}
