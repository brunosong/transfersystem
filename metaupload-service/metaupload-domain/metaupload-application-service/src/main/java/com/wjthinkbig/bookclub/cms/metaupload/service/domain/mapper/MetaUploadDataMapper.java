package com.wjthinkbig.bookclub.cms.metaupload.service.domain.mapper;

import com.wjthinkbig.bookclub.cms.metaupload.service.entity.MetaItem;
import com.wjthinkbig.bookclub.cms.metaupload.service.entity.Goods;
import com.wjthinkbig.bookclub.cms.metaupload.service.entity.GoodsLevelAttr;
import com.wjthinkbig.bookclub.cms.metaupload.service.entity.GoodsProperties;
import com.wjthinkbig.bookclub.cms.metaupload.service.valueobject.YesNoStatus;
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
