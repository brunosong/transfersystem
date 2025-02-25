package com.brunosong.transfer.system.metaupload.service.dataaccess.goods.adapter;

import com.brunosong.transfer.system.metaupload.service.domain.ports.output.repository.GoodsRepository;
import com.brunosong.transfer.system.metaupload.service.entity.Goods;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class GoodRepositoryImpl implements GoodsRepository {

    @Override
    public Optional<Goods> findGoods(Long goodsSeq) {
        return Optional.empty();
    }

    @Override
    public Optional<List<Goods>> findUpGoodsList(List<Long> upGoodsSeqList) {
        return Optional.empty();
    }

    @Override
    public void saveGoodsList(List<Goods> goodsList) {

    }
}
