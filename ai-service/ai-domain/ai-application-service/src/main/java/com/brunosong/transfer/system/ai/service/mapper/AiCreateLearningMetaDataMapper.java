package com.brunosong.transfer.system.ai.service.mapper;

import com.brunosong.transfer.system.ai.service.domain.entity.Chapter;
import com.brunosong.transfer.system.ai.service.domain.entity.Course;
import com.brunosong.transfer.system.ai.service.domain.entity.LearningMaterial;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class AiCreateLearningMetaDataMapper {

    public Course learningMaterialToCourse(LearningMaterial learningMaterial) {
        return Course.builder()
                .courseSeq(learningMaterial.getId().getValue())
                .courseName(learningMaterial.getTitle())
                .chapterList(learningMaterial.getMetadataList().stream().map( meta ->
                        Chapter.builder()
                                .build()
                ).collect(Collectors.toList())
                ).build();
    }

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
