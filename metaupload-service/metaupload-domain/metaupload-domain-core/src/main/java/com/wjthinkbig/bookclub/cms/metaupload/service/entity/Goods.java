package com.wjthinkbig.bookclub.cms.metaupload.service.entity;

import com.wjthinkbig.bookclub.cms.metaupload.service.valueobject.YesNoStatus;
import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Goods {

    private Long goodsSeq;
    private Long topUpSeq;
    private Long upSeq;
    private String summary;
    private int levels;
    private int orders;
    private String goodsGubun;
    private YesNoStatus qstWritLinkYn;
    private String writYn;
    private String useYn;
    private String svcTranDtime;
    private String oldCode;
    private List<GoodsProperties> goodsProperties;

    public void initTopUpSeqAndUpSeqAndLevel(Goods upGoods) {
        this.topUpSeq = upGoods.getTopUpSeq();
        this.upSeq = upGoods.getGoodsSeq();
        this.levels = upGoods.getLevels() + 1;
    }

    public void setSummary(String keyword) {
        this.summary = keyword;
    }

    public void setGoodsGubun(String goodsGubun) {
        this.goodsGubun = goodsGubun;
    }

    public void setGoodsProperties(List<GoodsProperties> goodsProperties) {
        this.goodsProperties = goodsProperties;
    }

}
