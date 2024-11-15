package com.wjthinkbig.bookclub.cms.metaupload.service.entity;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GoodsProperties {

    private Long masterSeq;
    private Long goodsSeq;
    private Long itemSeq;
    private String metaValue;

}
