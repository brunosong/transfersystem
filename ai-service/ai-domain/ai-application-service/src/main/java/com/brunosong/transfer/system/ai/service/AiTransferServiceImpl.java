package com.brunosong.transfer.system.ai.service;

import com.brunosong.transfer.system.ai.service.ports.input.AiTransferService;
import com.brunosong.transfer.system.ai.service.ports.output.repository.ChapterRepository;
import com.brunosong.transfer.system.ai.service.ports.output.repository.CourseRepository;
import org.springframework.stereotype.Service;

@Service
public class AiTransferServiceImpl implements AiTransferService {

    private final ChapterRepository chapterRepository;
    private final CourseRepository courseRepository;

    public AiTransferServiceImpl(ChapterRepository chapterRepository, CourseRepository courseRepository) {
        this.chapterRepository = chapterRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public void saveLearningMaterial() {

    }
}
