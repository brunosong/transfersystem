package com.brunosong.transfer.system.metaupload.service.domain.ports.output.repository;

import com.brunosong.transfer.system.metaupload.service.entity.Goods;

import java.util.List;
import java.util.Optional;

public interface GoodsRepository {

    Optional<Goods> findGoods(Long goodsSeq);

    Optional<List<Goods>> findUpGoodsList(List<Long> upGoodsSeqList);

    void saveGoodsList(List<Goods> goodsList);

}
