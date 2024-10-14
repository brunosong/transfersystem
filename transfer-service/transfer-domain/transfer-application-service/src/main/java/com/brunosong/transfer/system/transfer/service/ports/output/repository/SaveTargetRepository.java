package com.brunosong.transfer.system.transfer.service.ports.output.repository;

import com.brunosong.transfer.system.transfer.service.entity.Chapter;
import com.brunosong.transfer.system.transfer.service.entity.Course;
import com.brunosong.transfer.system.transfer.service.entity.LearningMaterial;

import java.util.List;

public interface SaveTargetRepository {

    void saveLearningMetadata(LearningMaterial learningMaterial);
    void saveMaterialLevelOne(Course course);
    void saveChapters(List<Chapter> chapterList);
}
