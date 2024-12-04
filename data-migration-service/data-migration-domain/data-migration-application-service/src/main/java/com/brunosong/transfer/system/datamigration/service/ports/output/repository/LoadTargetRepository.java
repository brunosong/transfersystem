package com.brunosong.transfer.system.datamigration.service.ports.output.repository;

import com.brunosong.transfer.system.datamigration.service.domain.entity.LoadTarget;

public interface LoadTargetRepository {
    LoadTarget save(LoadTarget loadTarget);
}
