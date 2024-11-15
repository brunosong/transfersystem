package com.brunosong.transfer.system.metaupload.service.domain;

import com.brunosong.transfer.system.metaupload.service.domain.helper.level.MetaUploadLevelThreeCreateHelper;
import com.brunosong.transfer.system.metaupload.service.domain.mapper.MetaUploadDataMapper;
import com.brunosong.transfer.system.metaupload.service.entity.Goods;
import com.brunosong.transfer.system.metaupload.service.entity.MetaItem;
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
