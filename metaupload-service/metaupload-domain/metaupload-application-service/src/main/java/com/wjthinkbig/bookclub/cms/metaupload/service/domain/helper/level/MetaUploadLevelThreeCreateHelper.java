package com.wjthinkbig.bookclub.cms.metaupload.service.domain.helper.level;

import com.wjthinkbig.bookclub.cms.metaupload.service.MetaUploadDomainService;
import com.wjthinkbig.bookclub.cms.metaupload.service.domain.helper.goods.MetaUploadCreateGoodsHelper;
import com.wjthinkbig.bookclub.cms.metaupload.service.domain.helper.goods.MetaUploadCreateGoodsPropertiesHelper;
import com.wjthinkbig.bookclub.cms.metaupload.service.entity.MetaItem;
import com.wjthinkbig.bookclub.cms.metaupload.service.valueobject.MetaItemLevelThreeGroupKey;
import com.wjthinkbig.bookclub.cms.metaupload.service.domain.mapper.MetaUploadDataMapper;
import com.wjthinkbig.bookclub.cms.metaupload.service.domain.ports.output.repository.GoodsRepository;
import com.wjthinkbig.bookclub.cms.metaupload.service.entity.Goods;
import com.wjthinkbig.bookclub.cms.metaupload.service.exception.GoodsNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MetaUploadLevelThreeCreateHelper extends MetaUploadLevelCreateHelper {

    /* 레벨 3 로직 */

    // 상위레벨에 정보를 가져오기위함
    private final GoodsRepository goodsRepository;
    private final MetaUploadDomainService metaUploadDomainService;
    private final MetaUploadDataMapper metaUploadDataMapper;

    public MetaUploadLevelThreeCreateHelper(GoodsRepository goodsRepository,
                                            MetaUploadDomainService metaUploadDomainService,
                                            MetaUploadDataMapper metaUploadDataMapper,
                                            MetaUploadCreateGoodsHelper goodsHelper,
                                            MetaUploadCreateGoodsPropertiesHelper goodsPropertiesHelper) {
        super(goodsHelper, goodsPropertiesHelper);
        this.goodsRepository = goodsRepository;
        this.metaUploadDomainService = metaUploadDomainService;
        this.metaUploadDataMapper = metaUploadDataMapper;
    }

    @Transactional
    public List<Goods> persistLevelThree(List<MetaItem> metaItemList) {

        checkLevelTwoGoodsSeq(metaItemList);

        //단원(Level3)만 추출
        List<MetaItem> levelThreeMetaItems = groupByLevelThree(metaItemList);

        //upGoods 정보를 한번에 가져온다
        List<Long> upGoodsSeqList = extractUpGoodsSeq(levelThreeMetaItems, MetaItem::getLevelTwoSeq);
        List<Goods> upGoodsList = findUpGoodsList(upGoodsSeqList);

        List<Goods> levelThreeGoods = createLevelThreeGoods(levelThreeMetaItems, upGoodsList);
        goodsRepository.saveGoodsList(levelThreeGoods);

        return levelThreeGoods;
    }

    public List<Goods> createLevelThreeGoods(List<MetaItem> levelThreeMetaItems, List<Goods> upGoodsList) {
        List<Goods> goodsList = new ArrayList<>();
        for (MetaItem item : levelThreeMetaItems) {
            Goods goods = createGoodsAndBaseSetting(item,
                                                    item.getLevelTwoSeq(),
                                                    upGoodsList,
                                                    concatKeyword(item)
                                                );
            goodsList.add(goods);
        }
        return goodsList;
    }

    public List<MetaItem> groupByLevelThree(List<MetaItem> metaItemList) {
        Map<MetaItemLevelThreeGroupKey, List<MetaItem>> grouping = metaItemList.stream().collect(Collectors.groupingBy(metaItem ->
                new MetaItemLevelThreeGroupKey(metaItem.getCourseCode(),
                        metaItem.getGrade(),
                        metaItem.getTerm(),
                        metaItem.getUnitName(),
                        metaItem.getUnitNum(),
                        metaItem.getPrePro(),
                        metaItem.getPubName(),
                        metaItem.getKeyword())));

        //첫번째 값들만 가져와야 DB에 GroupBy와 같은 효과를 낸다.
        return grouping.entrySet().stream()
                .map(entry -> entry.getValue().get(0))
                .collect(Collectors.toList());
    }

    private String concatKeyword(MetaItem item) {
        return String.join("/",
                    item.getKeyword(),
                    (item.getPubName() != null ? item.getPubName() : ""),
                    item.getGrade(),
                    (item.getTerm() != null ? item.getTerm() : ""),
                    (item.getPrePro() != null ? item.getPrePro() : ""),
                    String.valueOf(item.getUnitNum()),
                    item.getUnitName()
        );
    }

    public void checkLevelTwoGoodsSeq(List<MetaItem> metaItemList) {
        for (int i = 0; i < metaItemList.size(); i++) {
            if (metaItemList.get(i).getLevelTwoSeq().equals("") || metaItemList.get(i).getLevelTwoSeq() == null) {
                throw new GoodsNotFoundException("Could not found metaItem in twoLevelGoodsSeq for item is index : " + i);
            }
        }
    }
}
