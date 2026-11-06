package com.brunosong.deployment.system.targetagent.service.dataaccess.onlinecampus.adapter;

import com.brunosong.deployment.system.targetagent.service.domain.valueobject.ChunkContent;
import com.brunosong.deployment.system.targetagent.service.dto.message.ApplyOutcome;
import com.brunosong.deployment.system.targetagent.service.ports.output.repository.CurriculumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CurriculumRepositoryImpl implements CurriculumRepository {

    private final CurriculumPersistHandler curriculumPersistHandler;

    @Override
    @Transactional
    public ApplyOutcome saveOrUpdate(String curriculumId, ChunkContent content) {
        return switch (content) {
            case ChunkContent.Skeleton skeleton -> curriculumPersistHandler.curriculumToLesson(curriculumId, skeleton);
            case ChunkContent.SubjectChunk chunk -> curriculumPersistHandler.subjectToLesson(chunk);
        };
    }
}
