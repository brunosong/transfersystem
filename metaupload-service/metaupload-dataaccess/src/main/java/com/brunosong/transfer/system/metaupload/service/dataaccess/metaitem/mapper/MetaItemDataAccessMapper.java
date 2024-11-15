package com.brunosong.transfer.system.metaupload.service.dataaccess.metaitem.mapper;


import com.brunosong.transfer.system.metaupload.service.dataaccess.metaitem.entity.MetaUploadLogEntity;
import com.brunosong.transfer.system.metaupload.service.entity.MetaUploadLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MetaItemDataAccessMapper {


    public MetaUploadLogEntity metaUploadLogToMetaUploadLogEntity(MetaUploadLog metaUploadLog) {
       return MetaUploadLogEntity.builder()
               .metaRedisId(metaUploadLog.getMetaRedisId())
               .excelFileName(metaUploadLog.getExcelFileName())
               .adminId(metaUploadLog.getAdminId())
               .build();
    }

    public MetaUploadLog metaUploadLogEntityToMetaUploadLog(MetaUploadLogEntity metaUploadLogEntity) {
        return MetaUploadLog.builder()
                .metaRedisId(metaUploadLogEntity.getMetaRedisId())
                .excelFileName(metaUploadLogEntity.getExcelFileName())
                .adminId(metaUploadLogEntity.getAdminId())
                .build();
    }

}
