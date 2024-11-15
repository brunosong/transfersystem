package com.wjthinkbig.bookclub.cms.metaupload.service.entity;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetaUploadLog {

    private Integer metabulkUploadLogSeq;
    private UUID metaRedisId;
    private String metabulkType;
    private String topUpSeq;
    private String rollbackYn;
    private String adminId;
    private String adminIp;
    private LocalDateTime regDate;
    private String excelFileName;
    private String startLv;
    private String levelsKeyJsonString;
    private String updateAdminId;
    private LocalDateTime updateDate;

}
