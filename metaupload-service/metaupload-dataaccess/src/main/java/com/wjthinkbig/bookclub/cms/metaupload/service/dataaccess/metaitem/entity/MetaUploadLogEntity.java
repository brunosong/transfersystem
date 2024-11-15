package com.wjthinkbig.bookclub.cms.metaupload.service.dataaccess.metaitem.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@Entity
@Table(name = "tb_metabulk_upload_log")
@NoArgsConstructor
@AllArgsConstructor
public class MetaUploadLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "metabulk_upload_log_seq")
    private Integer metabulkUploadLogSeq;

    private UUID metaRedisId;

    @Column(name = "metabulk_type", length = 20)
    private String metabulkType;

    @Column(name = "top_up_seq", length = 10)
    private String topUpSeq;

    @Column(name = "rollback_yn", length = 3)
    private String rollbackYn = "N";

    @Column(name = "admin_id", length = 20)
    private String adminId;

    @Column(name = "admin_ip", length = 30)
    private String adminIp;

    @Column(name = "reg_date", columnDefinition = "datetime default current_timestamp()")
    private LocalDateTime regDate;

    @Column(name = "excel_file_name", length = 300, nullable = false)
    private String excelFileName;

    @Column(name = "start_lv", length = 20, nullable = false)
    private String startLv;

    @Column(name = "levels_key_json_string", columnDefinition = "text")
    private String levelsKeyJsonString;

    @Column(name = "update_admin_id", length = 20)
    private String updateAdminId;

    @Column(name = "update_date")
    private LocalDateTime updateDate;

}
