package com.wjthinkbig.bookclub.cms.metaupload.service.domain.dto;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.List;

@Data
public class MetaUploadCreateRequest {

    private String studyType;
    private String admId;
    private String metaCode;
    private String lv;
    private String startLevel;
    private String topUpSeq;
    private String excelFileName;
    private List<LinkedHashMap<String, Object>> excelMapList;

}
