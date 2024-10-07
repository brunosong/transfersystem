package com.brunosong.transfer.system.transfer.service.ports.output.repository;

import com.brunosong.transfer.system.transfer.service.entity.Chapter;

import java.util.List;

public interface ChapterRepository {
    List<Chapter> findAll();
}
