package com.brunosong.transfersystem.aiservice.mapper;

import com.brunosong.transfersystem.aiservice.dto.chap.AiChapDto.AiChapSaveDto;
import com.brunosong.transfersystem.main.dto.TranDto.ChapTranDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import static com.brunosong.transfersystem.aiservice.dto.kafka.KafkaReceiveDto.KafkaChapDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ChapMapper {

    @Mapping(source = "chapSeq", target = "aiChapSeq")
    @Mapping(source = "chapTitle", target = "aiChapTitle")
    @Mapping(source = "chapType", target = "aiChapType")
    AiChapSaveDto toChapSaveDto(ChapTranDto chapTranDto);

    @Mapping(source = "chapSeq", target = "aiChapSeq")
    @Mapping(source = "chapTitle", target = "aiChapTitle")
    @Mapping(source = "chapType", target = "aiChapType")
    AiChapSaveDto kafkaDtoToChapSaveDto(KafkaChapDto kafkaChapDto);


}
