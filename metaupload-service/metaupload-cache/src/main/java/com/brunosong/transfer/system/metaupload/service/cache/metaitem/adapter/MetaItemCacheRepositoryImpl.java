package com.brunosong.transfer.system.metaupload.service.cache.metaitem.adapter;

import com.brunosong.transfer.system.metaupload.service.cache.metaitem.repository.MetaItemRedisRepository;
import com.brunosong.transfer.system.metaupload.service.entity.MetaItem;
import com.brunosong.transfer.system.metaupload.service.domain.ports.output.repository.MetaItemCacheRepository;
import com.brunosong.transfer.system.metaupload.service.entity.Goods;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class MetaItemCacheRepositoryImpl implements MetaItemCacheRepository {

    private final MetaItemRedisRepository metaItemRedisRepository;

    public MetaItemCacheRepositoryImpl(MetaItemRedisRepository metaItemRedisRepository) {
        this.metaItemRedisRepository = metaItemRedisRepository;
    }

    @Override
    public String saveMetaItemList(List<MetaItem> metaItemList) {
        return metaItemRedisRepository.saveMetaItemList(metaItemList);
    }

    @Override
    public String saveLevelThreeGoods(List<Goods> goodsList) {
        return null;
    }
}
