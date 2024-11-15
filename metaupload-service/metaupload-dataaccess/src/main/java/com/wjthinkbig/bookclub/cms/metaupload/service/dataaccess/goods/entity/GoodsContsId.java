package com.wjthinkbig.bookclub.cms.metaupload.service.dataaccess.goods.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsContsId implements Serializable {

    private Long gcSeq;

    private Long goodsMetaMaster;

}
