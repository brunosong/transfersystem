package com.wjthinkbig.bookclub.cms.metaupload.service.domain.helper.level;

import com.wjthinkbig.bookclub.cms.metaupload.service.domain.helper.goods.MetaUploadCreateGoodsHelper;
import com.wjthinkbig.bookclub.cms.metaupload.service.domain.helper.goods.MetaUploadCreateGoodsPropertiesHelper;
import com.wjthinkbig.bookclub.cms.metaupload.service.entity.MetaItem;
import com.wjthinkbig.bookclub.cms.metaupload.service.entity.Goods;

import java.util.List;
import java.util.function.Function;

public abstract class MetaUploadLevelCreateHelper {

    private final MetaUploadCreateGoodsHelper goodsHelper;
    private final MetaUploadCreateGoodsPropertiesHelper goodsPropertiesHelper;

    protected MetaUploadLevelCreateHelper(MetaUploadCreateGoodsHelper goodsHelper,
                                          MetaUploadCreateGoodsPropertiesHelper goodsPropertiesHelper) {
        this.goodsHelper = goodsHelper;
        this.goodsPropertiesHelper = goodsPropertiesHelper;
    }

    public List<Long> extractUpGoodsSeq(List<MetaItem> metaItems, Function<MetaItem,Long> mapper) {
        return goodsHelper.extractUpGoodsSeq(metaItems,mapper);
    }

    public List<Goods> findUpGoodsList(List<Long> upGoodsList) {
        return goodsHelper.findUpGoodsList(upGoodsList);
    }

    public Goods createGoodsAndBaseSetting(MetaItem metaItem, Long upGoodsSeq, List<Goods> upGoodsList, String summery) {
        Goods goods = goodsHelper.createGoodsAndBaseSetting(metaItem, goodsHelper.extractGoods(upGoodsSeq, upGoodsList));
        goods.setSummary(summery);
        return goods;
    }

}
