package com.brunosong.transfer.system.metaupload.service.domain;

import com.brunosong.transfer.system.metaupload.service.MetaUploadDomainService;
import com.brunosong.transfer.system.metaupload.service.MetaUploadDomainServiceImpl;
import com.brunosong.transfer.system.metaupload.service.entity.MetaItem;
import com.brunosong.transfer.system.metaupload.service.domain.helper.goods.MetaUploadCreateGoodsHelper;
import com.brunosong.transfer.system.metaupload.service.domain.mapper.MetaUploadDataMapper;
import com.brunosong.transfer.system.metaupload.service.domain.ports.output.repository.GoodsRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@SpringJUnitConfig(value = {MetaUploadCreateGoodsHelper.class, MetaUploadDataMapper.class, MetaUploadCreateGoodsHelperTest.Config.class })
class MetaUploadCreateGoodsHelperTest {

    @MockBean
    private GoodsRepository goodsRepository;

    @Autowired
    MetaUploadCreateGoodsHelper metaUploadCreateGoodsHelper;

    @TestConfiguration
    static class Config {
        @Bean
        public MetaUploadDomainService metaUploadDomainService() {
            return new MetaUploadDomainServiceImpl();
        }
    }

    List<MetaItem> metaItemList;

    @BeforeEach
    void setup() {

        metaItemList = new ArrayList<>();

        // 첫 번째 행
        metaItemList.add(new MetaItem("1", "5학년", "2학기", "", "진도",
                "", "단원요점정리", "1", "사회", "", "교학사",
                "사회", 3, 1, "1-1. 나라의 등장과 발전", "요점정리", "Y", "1",
                "1-1. 나라의 등장과 발전", "", 511111L, ""));
    }

    @Test
    void UP_GOODS_SEQ를_추출한다() {
        List<Long> longs = metaUploadCreateGoodsHelper.extractUpGoodsSeq(metaItemList, MetaItem::getLevelTwoSeq);
        Assertions.assertThat(longs).containsExactlyInAnyOrder(511111L);
    }

}