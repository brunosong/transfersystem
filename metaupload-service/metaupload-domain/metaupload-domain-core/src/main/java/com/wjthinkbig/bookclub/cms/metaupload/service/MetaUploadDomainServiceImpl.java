package com.wjthinkbig.bookclub.cms.metaupload.service;


import com.wjthinkbig.bookclub.cms.metaupload.service.entity.Goods;

public class MetaUploadDomainServiceImpl implements MetaUploadDomainService {

    public void validateAndInitialUpSeqAndTopUpSeq(Goods goods, Goods upGoods) {

        goods.initTopUpSeqAndUpSeqAndLevel(upGoods);

    }



}
