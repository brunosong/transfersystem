package com.brunosong.deployment.system.authoring.service.ports.output;

import com.brunosong.deployment.system.authoring.service.entity.SkeletonDraft;

public interface SkeletonDraftRepository {
    void save(SkeletonDraft draft);
}
