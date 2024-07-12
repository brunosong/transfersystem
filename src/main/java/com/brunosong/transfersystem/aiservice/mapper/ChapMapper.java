package com.brunosong.transfersystem.aiservice.mapper;

import com.brunosong.transfersystem.aiservice.dto.chap.ChapDto.ChapSaveDto;
import com.brunosong.transfersystem.main.dto.TranDto.ChapTranDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ChapMapper {

    ChapSaveDto toChapSaveDto(ChapTranDto chapTranDto);


}
