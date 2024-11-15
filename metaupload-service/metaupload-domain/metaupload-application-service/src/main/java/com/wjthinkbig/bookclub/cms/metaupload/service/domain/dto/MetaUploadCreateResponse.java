package com.wjthinkbig.bookclub.cms.metaupload.service.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class MetaUploadCreateResponse<T> {

    private Status status;
    private String message;
    private String serverErrorMessage;
    private T data;

    public enum Status {
        SUCCESS,
        ERROR;
    }

}
