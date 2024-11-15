package com.wjthinkbig.bookclub.cms.metaupload.service.domain.ports.output.repository;

import com.wjthinkbig.bookclub.cms.metaupload.service.entity.GoodsLevelAttr;

import java.util.List;
import java.util.Optional;

public interface GoodsLevelAttrRepository {

    Optional<List<GoodsLevelAttr>> findGoodsMetaLevelAttr(Long goodsSeq, int level);
}
