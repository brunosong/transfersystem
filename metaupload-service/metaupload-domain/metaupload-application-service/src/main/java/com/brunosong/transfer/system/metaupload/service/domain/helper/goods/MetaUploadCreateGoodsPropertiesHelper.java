package com.brunosong.transfer.system.metaupload.service.domain.helper.goods;

import com.brunosong.transfer.system.metaupload.service.domain.mapper.MetaUploadDataMapper;
import com.brunosong.transfer.system.metaupload.service.domain.ports.output.repository.GoodsLevelAttrRepository;
import com.brunosong.transfer.system.metaupload.service.entity.GoodsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MetaUploadCreateGoodsPropertiesHelper {

    private final GoodsLevelAttrRepository goodsLevelAttrRepository;
    private final MetaUploadDataMapper metaUploadDataMapper;

    public MetaUploadCreateGoodsPropertiesHelper(GoodsLevelAttrRepository goodsLevelAttrRepository,
                                                 MetaUploadDataMapper metaUploadDataMapper) {
        this.goodsLevelAttrRepository = goodsLevelAttrRepository;
        this.metaUploadDataMapper = metaUploadDataMapper;
    }

    public List<GoodsProperties> createGoodsProperties(Long topUpSeq, int level) {
        return goodsLevelAttrRepository.findGoodsMetaLevelAttr(topUpSeq, level)
                                            .map(goodsLevelAttrs ->
                                                    goodsLevelAttrs.stream()
                                                    .map(metaUploadDataMapper::goodsLevelAttrToGoodsProperties)
                                                    .collect(Collectors.toList()))
                                            .get();
    }


}
