package com.brunosong.transfer.system.metaupload.service.domain.ports.output.repository;

import com.brunosong.transfer.system.metaupload.service.entity.Goods;
import com.brunosong.transfer.system.metaupload.service.entity.MetaItem;

import java.util.List;

public interface MetaItemCacheRepository {

    String saveMetaItemList(List<MetaItem> metaItemList);

    String saveLevelThreeGoods(List<Goods> goodsList);

}
