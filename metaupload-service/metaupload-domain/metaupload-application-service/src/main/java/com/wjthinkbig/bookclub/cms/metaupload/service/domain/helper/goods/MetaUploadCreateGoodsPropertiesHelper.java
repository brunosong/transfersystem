package com.wjthinkbig.bookclub.cms.metaupload.service.domain.helper.goods;

import com.wjthinkbig.bookclub.cms.metaupload.service.domain.mapper.MetaUploadDataMapper;
import com.wjthinkbig.bookclub.cms.metaupload.service.domain.ports.output.repository.GoodsLevelAttrRepository;
import com.wjthinkbig.bookclub.cms.metaupload.service.entity.GoodsProperties;
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
