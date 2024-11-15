package com.wjthinkbig.bookclub.cms.metaupload.service.dataaccess.goods.adapter;

import com.wjthinkbig.bookclub.cms.metaupload.service.domain.ports.output.repository.GoodsLevelAttrRepository;
import com.wjthinkbig.bookclub.cms.metaupload.service.entity.GoodsLevelAttr;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class GoodsLevelAttrRepositoryImpl implements GoodsLevelAttrRepository {

    @Override
    public Optional<List<GoodsLevelAttr>> findGoodsMetaLevelAttr(Long goodsSeq, int level) {
        return Optional.empty();
    }

}
