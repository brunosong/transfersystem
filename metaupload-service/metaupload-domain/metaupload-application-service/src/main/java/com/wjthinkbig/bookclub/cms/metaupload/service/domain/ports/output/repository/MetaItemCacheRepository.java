package com.wjthinkbig.bookclub.cms.metaupload.service.domain.ports.output.repository;

import com.wjthinkbig.bookclub.cms.metaupload.service.entity.MetaItem;
import com.wjthinkbig.bookclub.cms.metaupload.service.entity.Goods;

import java.util.List;

public interface MetaItemCacheRepository {

    String saveMetaItemList(List<MetaItem> metaItemList);

    String saveLevelThreeGoods(List<Goods> goodsList);

}
