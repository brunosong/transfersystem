package com.brunosong.transfer.system.ai.service.dataaccess.chapter.mapper;

import com.brunosong.transfer.system.ai.service.dataaccess.chapter.entity.ChapterEntity;
import com.brunosong.transfer.system.transfer.service.entity.Chapter;
import org.springframework.stereotype.Component;

@Component
public class ChapterDataAccessMapper {

    public Chapter chapterEntityToChapter(ChapterEntity chapterEntity) {
        return Chapter.builder()
                .chapSeq(chapterEntity.getChapSeq())
                .chapTitle(chapterEntity.getChapTitle())
                .chapType(chapterEntity.getChapType() != null ? chapterEntity.getChapType().name() : "")
                .chapOrder(chapterEntity.getChapOrder())
                .courseSeq(chapterEntity.getCourse().getCourseSeq())
                .build();
    }
}
