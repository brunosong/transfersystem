package com.brunosong.transfer.system.datamigration.service.mapper;

import org.springframework.stereotype.Component;

@Component
public class CreateLearningMetaDataMapper {

//    public Course learningMaterialToCourse(LearningMaterial learningMaterial) {
//        return Course.builder()
//                .courseSeq(learningMaterial.getId().getValue())
//                .courseName(learningMaterial.getTitle())
//                .chapterList(learningMaterial.getMetadataList().stream().map( meta ->
//                        Chapter.builder()
//                                .build()
//                ).collect(Collectors.toList())
//                ).build();
//    }

//    @Mapping(source = "chapSeq", target = "aiChapSeq")
//    @Mapping(source = "chapTitle", target = "aiChapTitle")
//    @Mapping(source = "chapType", target = "aiChapType")
//    AiChapSaveDto toChapSaveDto(ChapTranDto chapTranDto);
//
//    @Mapping(source = "chapSeq", target = "aiChapSeq")
//    @Mapping(source = "chapTitle", target = "aiChapTitle")
//    @Mapping(source = "chapType", target = "aiChapType")
//    AiChapSaveDto kafkaDtoToChapSaveDto(KafkaChapDto kafkaChapDto);

}
