package com.brunosong.transfer.system.metaupload.service.domain.helper.goods;

import com.brunosong.transfer.system.metaupload.service.MetaUploadDomainService;
import com.brunosong.transfer.system.metaupload.service.domain.mapper.MetaUploadDataMapper;
import com.brunosong.transfer.system.metaupload.service.entity.Goods;
import com.brunosong.transfer.system.metaupload.service.entity.MetaItem;
import com.brunosong.transfer.system.metaupload.service.exception.GoodsNotFoundException;
import com.brunosong.transfer.system.metaupload.service.domain.ports.output.repository.GoodsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MetaUploadCreateGoodsHelper {

    private final GoodsRepository goodsRepository;
    private final MetaUploadDataMapper metaUploadDataMapper;
    private final MetaUploadDomainService metaUploadDomainService;

    public MetaUploadCreateGoodsHelper(GoodsRepository goodsRepository,
                                       MetaUploadDataMapper metaUploadDataMapper,
                                       MetaUploadDomainService metaUploadDomainService) {
        this.goodsRepository = goodsRepository;
        this.metaUploadDataMapper = metaUploadDataMapper;
        this.metaUploadDomainService = metaUploadDomainService;
    }

    public Goods createGoodsAndBaseSetting(MetaItem metaItem, Goods upGoods) {
        Goods goods = metaUploadDataMapper.metaItemToGoods(metaItem);
        metaUploadDomainService.validateAndInitialUpSeqAndTopUpSeq(goods, upGoods);
        goods.setGoodsGubun(upGoods.getGoodsGubun());
        return goods;
    }

    public List<Long> extractUpGoodsSeq(List<MetaItem> metaItems, Function<MetaItem,Long> mapper) {
        return metaItems.stream()
                .map(mapper)
                .collect(Collectors.toList());
    }

    public List<Goods> findUpGoodsList(List<Long> upGoodsList) {
        return goodsRepository.findUpGoodsList(upGoodsList)
                            .orElseThrow(() -> {
                                log.error("Could not found upGoodsSeq list");
                                return new GoodsNotFoundException("Could not found upGoodsSeq list");
                            });
    }

    public Goods extractGoods(Long goodsSeq, List<Goods> goodsList) {
        Goods upGoods = goodsList.stream()
                .filter(g -> goodsSeq.equals(g.getGoodsSeq()))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Could not found goodsSeq for goodsSeq is {}", goodsSeq);
                    return new GoodsNotFoundException("Could not found goodsSeq for goodsSeq is " + goodsSeq);
                });
        return upGoods;
    }


}
