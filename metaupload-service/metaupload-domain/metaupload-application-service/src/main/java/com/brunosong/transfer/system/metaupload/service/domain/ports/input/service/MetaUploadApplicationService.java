package com.brunosong.transfer.system.metaupload.service.domain.ports.input.service;

import com.brunosong.transfer.system.metaupload.service.domain.dto.MetaUploadCreateRequest;
import com.brunosong.transfer.system.metaupload.service.domain.dto.MetaUploadCreateResponse;

import javax.validation.Valid;

public interface MetaUploadApplicationService {

    MetaUploadCreateResponse metaUpload(@Valid MetaUploadCreateRequest metaUploadCreateRequest);

}
