package com.brunosong.transfer.system.metaupload.service.domain.helper.level;

import com.brunosong.transfer.system.metaupload.service.entity.Goods;
import com.brunosong.transfer.system.metaupload.service.entity.MetaItem;
import com.brunosong.transfer.system.metaupload.service.domain.helper.goods.MetaUploadCreateGoodsHelper;
import com.brunosong.transfer.system.metaupload.service.domain.helper.goods.MetaUploadCreateGoodsPropertiesHelper;

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
