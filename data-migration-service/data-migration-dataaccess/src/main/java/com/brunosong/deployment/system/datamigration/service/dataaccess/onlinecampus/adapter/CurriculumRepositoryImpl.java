package com.brunosong.deployment.system.datamigration.service.dataaccess.onlinecampus.adapter;

import com.brunosong.deployment.system.datamigration.service.domain.valueobject.ChunkContent;
import com.brunosong.deployment.system.datamigration.service.dto.message.DataMigrationDto;
import com.brunosong.deployment.system.datamigration.service.ports.output.repository.CurriculumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CurriculumRepositoryImpl implements CurriculumRepository {

    private final CurriculumPersistHandler curriculumPersistHandler;

    @Override
    @Transactional
    public DataMigrationDto saveOrUpdate(String curriculumId, ChunkContent content) {
        return switch (content) {
            case ChunkContent.Skeleton skeleton -> curriculumPersistHandler.curriculumToLesson(curriculumId, skeleton);
            case ChunkContent.SubjectChunk chunk -> curriculumPersistHandler.subjectToLesson(chunk);
        };
    }
}
