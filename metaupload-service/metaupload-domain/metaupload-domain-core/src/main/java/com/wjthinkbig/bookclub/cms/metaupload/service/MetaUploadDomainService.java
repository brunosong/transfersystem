package com.wjthinkbig.bookclub.cms.metaupload.service;


import com.wjthinkbig.bookclub.cms.metaupload.service.entity.Goods;

public interface MetaUploadDomainService {

    //메타업로드 도메인에서 어떤 문제를 해결해야하는걸까?
    //벨리데이션
    //오더 같은경우에는 지불, 캔슬, 오더승인, 캔슬오더
    //메타업로드는 어떤게 도메인에 들어가야 할까?
    void validateAndInitialUpSeqAndTopUpSeq(Goods goods, Goods upGoods);



}
