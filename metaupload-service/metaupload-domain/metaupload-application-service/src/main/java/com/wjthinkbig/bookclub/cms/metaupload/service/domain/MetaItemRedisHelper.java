package com.wjthinkbig.bookclub.cms.metaupload.service.domain;

import com.wjthinkbig.bookclub.cms.metaupload.service.entity.MetaItem;
import com.wjthinkbig.bookclub.cms.metaupload.service.domain.ports.output.repository.MetaItemCacheRepository;
import com.wjthinkbig.bookclub.cms.metaupload.service.entity.Goods;
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
