package com.brunosong.transfer.system.metaupload.service.cache.metaitem.repository;

import com.brunosong.transfer.system.metaupload.service.cache.metaitem.config.MockRedisConfig;
import com.brunosong.transfer.system.metaupload.service.cache.metaitem.mapper.MetaItemCacheDataMapper;
import com.brunosong.transfer.system.metaupload.service.entity.MetaItem;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.ArrayList;
import java.util.List;

@SpringJUnitConfig(value = {MetaItemRedisRepository.class, MockRedisConfig.class, MetaItemCacheDataMapper.class})
class MetaItemRedisRepositoryTest {

    @Autowired
    private MetaItemRedisRepository metaItemRedisRepository;

    @Test
    void 기본_METAITEM_LIST를_레디스에_저장_테스트() {

        List<MetaItem> metaItemList = new ArrayList<>();

        // 첫 번째 행
        metaItemList.add(new MetaItem("1", "5학년", "2학기", "", "진도",
                "", "단원요점정리", "1", "사회", "", "교학사",
                "사회", 3, 1, "1-1. 나라의 등장과 발전", "요점정리", "Y", "1",
                "1-1. 나라의 등장과 발전", "", 511111L,""));

        String key = metaItemRedisRepository.saveMetaItemList(metaItemList);
        List<MetaItem> getRedisResult = metaItemRedisRepository.getOriginalMetaList(key);

        Assertions.assertThat(metaItemList).hasSize(getRedisResult.size());
    }

}