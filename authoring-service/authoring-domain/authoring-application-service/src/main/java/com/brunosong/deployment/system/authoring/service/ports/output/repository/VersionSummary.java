package com.brunosong.deployment.system.authoring.service.ports.output.repository;

import com.brunosong.deployment.system.authoring.service.entity.CurriculumVersion;

import java.time.Instant;

/** 판 기록 한 줄. 판의 트리(내용)는 커서 빼고, 언제 어떤 초안으로 만들었는지만 담는다 */
public record VersionSummary(int version, Instant publishedAt, CurriculumVersion.BasedOn basedOn) {
}
