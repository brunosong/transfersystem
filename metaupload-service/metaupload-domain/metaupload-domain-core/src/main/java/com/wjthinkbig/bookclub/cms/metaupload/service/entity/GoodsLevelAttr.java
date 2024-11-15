package com.wjthinkbig.bookclub.cms.metaupload.service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GoodsLevelAttr {

    private Long glAttrSeq;
    private Long goodsSeq;
    private Long masterSeq;
    private int goodsLevels;
    private int orders;

}
