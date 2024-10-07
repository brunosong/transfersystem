package com.brunosong.transfer.system.transfer.service;

import com.brunosong.transfer.system.transfer.service.dto.excution.ExcutionTransferCommand;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.ChapterRepository;
import com.brunosong.transfer.system.transfer.service.ports.output.repository.CourseRepository;

public abstract class TransferSendHelper {

    protected final CourseRepository courseRepository;
    protected final ChapterRepository chapterRepository;

    public TransferSendHelper(CourseRepository courseRepository, ChapterRepository chapterRepository) {
        this.courseRepository = courseRepository;
        this.chapterRepository = chapterRepository;
    }

    abstract void courseTransferProcess(ExcutionTransferCommand excutionTransferCommand);
    abstract void chapterTransferProcess(ExcutionTransferCommand excutionTransferCommand);
}
