package com.brunosong.deployment.system.authoring.service.ports.output.repository;

import com.brunosong.deployment.system.authoring.service.entity.SkeletonDraft;

import java.util.List;

public record SkeletonDraftPage(List<SkeletonDraft> items, long total) {
}
