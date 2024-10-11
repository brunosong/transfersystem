package com.brunosong.transfer.system.target.service.dataaccess.aiservice.adapter;

import com.brunosong.transfer.system.transfer.service.entity.Chapter;
import com.brunosong.transfer.system.transfer.service.entity.Course;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.SaveTargetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class SaveTargetRepositoryImpl implements SaveTargetRepository {

    @Override
    public void saveCourse(Course course) {

    }

    @Override
    public void saveChapters(List<Chapter> chapterList) {

    }
}
