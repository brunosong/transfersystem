package com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.adapter;

import com.brunosong.transfer.system.datamigration.service.domain.valueobject.CurriculumKey;
import com.brunosong.transfer.system.datamigration.service.domain.valueobject.SourceContentData;
import com.brunosong.transfer.system.datamigration.service.dto.message.DataMigrationDto;
import com.brunosong.transfer.system.datamigration.service.ports.output.repository.CurriculumRepository;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class CurriculumRepositoryImpl implements CurriculumRepository {

    private final CurriculumPersistHandler curriculumPersistHandler;

    @Override
    @Transactional
    public DataMigrationDto saveOrUpdate(SourceContentData sourceContentData) {

        Document document = Document.parse(StandardCharsets.UTF_8.decode(sourceContentData.getJsonData()).toString());
        Document curriculumDoc = document.get(CurriculumKey.CURRICULUM.getKey(), Document.class);
        if (curriculumDoc != null) {
            return curriculumPersistHandler.curriculumToLesson(document);
        } else {
            return curriculumPersistHandler.subjectToLesson(document);
        }
    }

}
