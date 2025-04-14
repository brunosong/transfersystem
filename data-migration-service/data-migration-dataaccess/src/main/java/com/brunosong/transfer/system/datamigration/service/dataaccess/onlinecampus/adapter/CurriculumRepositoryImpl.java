package com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.adapter;

import com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.entity.*;
import com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.mapper.DataMigrationDataAccessMapper;
import com.brunosong.transfer.system.datamigration.service.dataaccess.onlinecampus.repository.CurriculumJpaRepository;
import com.brunosong.transfer.system.datamigration.service.domain.valueobject.SourceContentData;
import com.brunosong.transfer.system.datamigration.service.dto.message.DataMigrationDto;
import com.brunosong.transfer.system.datamigration.service.ports.output.repository.CurriculumRepository;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CurriculumRepositoryImpl implements CurriculumRepository {

    private final CurriculumJpaRepository curriculumJpaRepository;
    private final DataMigrationDataAccessMapper dataAccessMapper;

    @Override
    public DataMigrationDto save(SourceContentData sourceContentData) {

        Document document = Document.parse(StandardCharsets.UTF_8.decode(sourceContentData.getJsonData()).toString());
        CurriculumEntity curriculumEntity = dataAccessMapper.convertToCurriculum(document);

        Optional<CurriculumEntity> existing = curriculumJpaRepository.findByCurriculumId(curriculumEntity.getCurriculumId());

        if (existing.isPresent()) {
            CurriculumEntity existingCurriculumEntity = existing.get();
            existingCurriculumEntity.setDescription(curriculumEntity.getDescription());
            existingCurriculumEntity.setTitle(curriculumEntity.getTitle());
            existingCurriculumEntity.setGradeEntities(curriculumEntity.getGradeEntities());

            CurriculumEntity saveEntity = curriculumJpaRepository.save(existingCurriculumEntity);
            return dataAccessMapper.toDataMigrationDto(saveEntity);
        } else {
            curriculumEntity.getGradeEntities().forEach(grade -> grade.setCurriculumEntity(curriculumEntity));
            CurriculumEntity saveEntity = curriculumJpaRepository.save(curriculumEntity);
            return dataAccessMapper.toDataMigrationDto(saveEntity);
        }
    }


}
