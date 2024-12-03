package com.brunosong.transfer.system.loadtarget.service.dataaccess.chapter.mapper;

import com.brunosong.transfer.system.loadtarget.service.dataaccess.chapter.entity.ChapterEntity;
import com.brunosong.transfer.system.loadtarget.service.domain.entity.Chapter;
import org.springframework.stereotype.Component;

import static com.brunosong.transfer.system.loadtarget.service.dataaccess.chapter.entity.ChapterEntity.*;

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

    public ChapterEntity chapterToChapterEntity(Chapter chapter) {
        return ChapterEntity.builder()
                .chapSeq(chapter.getChapSeq())
                .chapTitle(chapter.getChapTitle())
                .chapType(ChapterTypeEnum.valueOf(chapter.getChapType()))
                .chapOrder(chapter.getChapOrder())
                .build();
    }
}
