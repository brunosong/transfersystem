package com.wjthinkbig.bookclub.cms.metaupload.service.exception;

import java.util.List;

public class MetaItemConvertException extends RuntimeException {

    private List<String> errorMessages;
    public MetaItemConvertException(String message, List<String> errorMessages) {
        super(message);
        this.errorMessages = errorMessages;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }
}
