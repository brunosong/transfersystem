package com.wjthinkbig.bookclub.cms.metaupload.service.domain;

import com.wjthinkbig.bookclub.cms.metaupload.service.entity.MetaItem;
import com.wjthinkbig.bookclub.cms.metaupload.service.domain.dto.MetaUploadCreateRequest;
import com.wjthinkbig.bookclub.cms.metaupload.service.domain.dto.MetaUploadCreateResponse;
import com.wjthinkbig.bookclub.cms.metaupload.service.domain.ports.input.service.MetaUploadApplicationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@Service
public class MetaUploadApplicationServiceImpl implements MetaUploadApplicationService {

    private final MetaUploadCreateHandler metaUploadCreateHandler;
    private final MetaUploadConvertHelper metaUploadConvertHelper;
    private final MetaItemRedisHelper metaItemRedisHelper;

    public MetaUploadApplicationServiceImpl(MetaUploadCreateHandler metaUploadCreateHandler,
                                            MetaUploadConvertHelper metaUploadConvertHelper,
                                            MetaItemRedisHelper metaItemRedisHelper) {
        this.metaUploadCreateHandler = metaUploadCreateHandler;
        this.metaUploadConvertHelper = metaUploadConvertHelper;
        this.metaItemRedisHelper = metaItemRedisHelper;
    }

    @Override
    @Transactional
    public MetaUploadCreateResponse metaUpload(MetaUploadCreateRequest metaUploadCreateRequest) {

        List<MetaItem> metaItemList = metaUploadConvertHelper
                                            .excelToMetaItemConvert(metaUploadCreateRequest.getExcelMapList());

        metaItemRedisHelper.saveOriginalMetaItemList(metaItemList);

        metaUploadCreateHandler.saveLevelThree(metaItemList);

        return null;
    }


}
