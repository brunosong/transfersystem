package com.wjthinkbig.bookclub.cms.metaupload.service.domain.ports.input.service;

import com.wjthinkbig.bookclub.cms.metaupload.service.domain.dto.MetaUploadCreateRequest;
import com.wjthinkbig.bookclub.cms.metaupload.service.domain.dto.MetaUploadCreateResponse;

import javax.validation.Valid;

public interface MetaUploadApplicationService {

    MetaUploadCreateResponse metaUpload(@Valid MetaUploadCreateRequest metaUploadCreateRequest);

}
