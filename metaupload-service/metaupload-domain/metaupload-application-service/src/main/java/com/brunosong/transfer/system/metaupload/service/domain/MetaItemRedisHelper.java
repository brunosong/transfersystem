package com.brunosong.transfer.system.metaupload.service.domain;

import com.brunosong.transfer.system.metaupload.service.domain.ports.output.repository.MetaItemCacheRepository;
import com.brunosong.transfer.system.metaupload.service.entity.Goods;
import com.brunosong.transfer.system.metaupload.service.entity.MetaItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MetaItemRedisHelper {

    private final MetaItemCacheRepository metaItemCacheRepository;

    public MetaItemRedisHelper(MetaItemCacheRepository metaItemCacheRepository) {
        this.metaItemCacheRepository = metaItemCacheRepository;
    }

    public String saveOriginalMetaItemList(List<MetaItem> metaItemList) {
        return metaItemCacheRepository.saveMetaItemList(metaItemList);
    }

    public String saveLevelThreeGoods(List<Goods> goodsList) {
        return metaItemCacheRepository.saveLevelThreeGoods(goodsList);
    }

}
