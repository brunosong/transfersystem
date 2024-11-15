package com.wjthinkbig.bookclub.cms.metaupload.service.domain;

import com.wjthinkbig.bookclub.cms.metaupload.service.MetaUploadDomainService;
import com.wjthinkbig.bookclub.cms.metaupload.service.MetaUploadDomainServiceImpl;
import com.wjthinkbig.bookclub.cms.metaupload.service.entity.MetaItem;
import com.wjthinkbig.bookclub.cms.metaupload.service.domain.helper.level.MetaUploadLevelThreeCreateHelper;
import com.wjthinkbig.bookclub.cms.metaupload.service.domain.mapper.MetaUploadDataMapper;
import com.wjthinkbig.bookclub.cms.metaupload.service.domain.ports.output.repository.GoodsRepository;
import com.wjthinkbig.bookclub.cms.metaupload.service.entity.Goods;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@SpringJUnitConfig(value = {MetaUploadLevelThreeCreateHelper.class, MetaUploadDataMapper.class})
@Import(MetaUploadLevelThreeCreateHelperTest.Config.class)
class MetaUploadLevelThreeCreateHelperTest {

    @TestConfiguration
    static class Config {
        @Bean
        public MetaUploadDomainService metaUploadDomainService() {
            return new MetaUploadDomainServiceImpl();
        }
    }

    @MockBean
    private GoodsRepository goodsRepository;

    @Autowired
    private MetaUploadLevelThreeCreateHelper metaUploadLevelThreeCreateHelper;

    List<MetaItem> metaItemList;

    @BeforeEach
    void setup() {

        metaItemList = new ArrayList<>();

        // 첫 번째 행
        metaItemList.add(new MetaItem("1", "5학년", "2학기", "", "진도",
                "", "단원요점정리", "1", "사회", "", "교학사",
                "사회", 3, 1, "1-1. 나라의 등장과 발전", "요점정리", "Y", "1",
                "1-1. 나라의 등장과 발전", "", 511111L,""));

        // 두 번째 행
        metaItemList.add(new MetaItem("2", "5학년", "2학기", "", "진도",
                "", "단원요점정리", "1", "사회", "", "교학사",
                "사회", 3, 2, "1-2. 독창적 문화를 발전시킨 고려", "요점정리", "Y", "1",
                "1-2. 독창적 문화를 발전시킨 고려", "", 511111L,""));

        // 세 번째 행
        metaItemList.add(new MetaItem("3", "5학년", "2학기", "", "진도", "", "단원요점정리", "1", "사회", "", "교학사",
                "사회", 3, 3, "1-3. 민족 문화를 지켜 나간 조선", "요점정리", "Y", "1", "1-3. 민족 문화를 지켜 나간 조선", "", 511111L,""));

        // 네 번째 행
        metaItemList.add(new MetaItem("4", "5학년", "2학기", "", "진도", "", "단원요점정리", "1", "사회", "", "교학사",
                "사회", 3, 4, "2-1. 새로운 사회를 향한 움직임", "요점정리", "Y", "1", "2-1. 새로운 사회를 향한 움직임", "", 511111L,""));

        // 다섯 번째 행
        metaItemList.add(new MetaItem("5", "5학년", "2학기", "", "진도", "", "단원요점정리", "1", "사회", "", "교학사",
                "사회", 3, 5, "2-2. 일제의 침략과 광복을 위한 노력", "요점정리", "Y", "1", "2-2. 일제의 침략과 광복을 위한 노력", "", 511111L,""));

        // 여섯 번째 행
        metaItemList.add(new MetaItem("6", "5학년", "2학기", "", "진도", "", "단원요점정리", "1", "사회", "", "교학사",
                "사회", 3, 6, "2-3. 대한민국 정부의 수립과 6ㆍ25 전쟁", "요점정리", "Y", "1", "2-3. 대한민국 정부의 수립과 6ㆍ25 전쟁", "", 511111L,""));

        // 일곱 번째 행
        metaItemList.add(new MetaItem("7", "6학년", "2학기", "", "진도", "", "단원요점정리", "1", "사회", "", "교학사",
                "사회", 3, 1, "1-1. 지구, 대륙 그리고 국가들", "요점정리", "Y", "1", "1-1. 지구, 대륙 그리고 국가들", "", 511111L,""));
    }

    @Test
    void 그룹바이가_정상적으로_동작한다() {
        metaItemList = new ArrayList<>();

        metaItemList.add(new MetaItem("1", "5학년", "2학기", "", "진도",
                "", "단원요점정리", "1", "사회", "", "교학사",
                "사회", 3, 1, "1-1. 나라의 등장과 발전", "요점정리", "Y", "1",
                "1-1. 나라의 등장과 발전", "", 511111L,""));

        metaItemList.add(new MetaItem("1", "5학년", "2학기", "", "진도",
                "", "단원요점정리", "1", "사회", "", "교학사",
                "사회", 3, 1, "1-1. 나라의 등장과 발전", "요점정리", "Y", "1",
                "1-1. 나라의 등장과 발전", "", 511111L,""));

        int expectedSize = 1;

        List<MetaItem> result = metaUploadLevelThreeCreateHelper.groupByLevelThree(metaItemList);

        assertEquals(expectedSize, result.size());
        MetaItem expectedMetaItem = new MetaItem("1", "5학년", "2학기", "", "진도",
                "", "단원요점정리", "1", "사회", "", "교학사",
                "사회", 3, 1, "1-1. 나라의 등장과 발전", "요점정리", "Y", "1",
                "1-1. 나라의 등장과 발전", "", 511111L, "");

        assertEquals(expectedMetaItem, result.get(0));
    }

    @Test
    void Goods_Entity_값이_정상적으로_만들어진다() {

//        List<Goods> upGoodsList = getMockUpGoodsList();
//
//        // 테스트 메서드 호출
//        List<Goods> result = metaUploadLevelThreeCreateHelper.goodsSetting(upGoodsList, metaItemList);
//
//        // 도메인 서비스를 제대로 호출했는지 확인
//        verify(metaUploadDomainService, times(metaItemList.size())).validateAndInitialUpSeqAndTopUpSeq(any(), any());
//
//        // 결과 검증
//        assertEquals(metaItemList.size(), result.size());
//
//        // 값이 제대로 셋팅 되었는지 확인
//        Assertions.assertThat(result.get(0).getTopUpSeq()).isNotNull();
//        Assertions.assertThat(result.get(0).getUpSeq()).isNotNull();
//        Assertions.assertThat(result.get(0).getLevels()).isNotNull();
    }


    @Test
    void 전체로직이_정상작동한다() {

        List<Goods> upGoodsList = getMockUpGoodsList();
        when(goodsRepository.findUpGoodsList(anyList())).thenReturn(Optional.of(upGoodsList));

        metaUploadLevelThreeCreateHelper.persistLevelThree(metaItemList);
    }

    public List<Goods> getMockUpGoodsList() {
        List<Goods> upGoodsList = new ArrayList<>();
        upGoodsList.add(
                Goods.builder()
                        .goodsSeq(511111L)
                        .topUpSeq(500000L)
                        .levels(2)
                        .goodsGubun("B")
                        .build()
        );
        return upGoodsList;
    }


}