package com.brunosong.transfer.system.metaupload.service;


import com.brunosong.transfer.system.metaupload.service.entity.Goods;

public class MetaUploadDomainServiceImpl implements MetaUploadDomainService {

    public void validateAndInitialUpSeqAndTopUpSeq(Goods goods, Goods upGoods) {

        goods.initTopUpSeqAndUpSeqAndLevel(upGoods);

    }



}
