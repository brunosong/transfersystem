package com.wjthinkbig.bookclub.cms.metaupload.service.domain;

import com.wjthinkbig.bookclub.cms.metaupload.service.entity.MetaItem;
import com.wjthinkbig.bookclub.cms.metaupload.service.domain.helper.level.MetaUploadLevelThreeCreateHelper;
import com.wjthinkbig.bookclub.cms.metaupload.service.domain.mapper.MetaUploadDataMapper;
import com.wjthinkbig.bookclub.cms.metaupload.service.entity.Goods;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
public class MetaUploadCreateHandler {

    private final MetaItemRedisHelper metaItemRedisHelper;
    private final MetaUploadLevelThreeCreateHelper metaUploadLevelThreeCreateHelper;
    private final MetaUploadDataMapper metaUploadDataMapper;

    public MetaUploadCreateHandler(MetaItemRedisHelper metaItemRedisHelper,
                                   MetaUploadLevelThreeCreateHelper metaUploadLevelThreeCreateHelper,
                                   MetaUploadDataMapper metaUploadDataMapper) {
        this.metaItemRedisHelper = metaItemRedisHelper;
        this.metaUploadLevelThreeCreateHelper = metaUploadLevelThreeCreateHelper;
        this.metaUploadDataMapper = metaUploadDataMapper;
    }

    @Transactional
    public String saveLevelThree(List<MetaItem> metaItemList) {

        // save db
        List<Goods> goodsList = metaUploadLevelThreeCreateHelper.persistLevelThree(metaItemList);

        // save level three cache
        metaItemRedisHelper.saveLevelThreeGoods(goodsList);

        return "ok";
    }

}
