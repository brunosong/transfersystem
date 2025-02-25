package com.brunosong.transfer.system.metaupload.service.domain.mapper;

import com.brunosong.transfer.system.metaupload.service.entity.Goods;
import com.brunosong.transfer.system.metaupload.service.entity.GoodsLevelAttr;
import com.brunosong.transfer.system.metaupload.service.entity.GoodsProperties;
import com.brunosong.transfer.system.metaupload.service.entity.MetaItem;
import com.brunosong.transfer.system.metaupload.service.valueobject.YesNoStatus;
import org.springframework.stereotype.Component;

@Component
public class MetaUploadDataMapper {

    public Goods metaItemToGoods(MetaItem metaItem) {
        return Goods.builder()
                .qstWritLinkYn(YesNoStatus.N)
                .orders(metaItem.getUnitNum())
                .build();
    }

    public GoodsProperties goodsLevelAttrToGoodsProperties(GoodsLevelAttr goodsLevelAttr) {
        return GoodsProperties.builder()
                        .masterSeq(goodsLevelAttr.getMasterSeq())
                        .goodsSeq(goodsLevelAttr.getGoodsSeq())
                        .build();
    }

}
