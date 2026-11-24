package com.brunosong.deployment.system.authoring.service.dataaccess.version.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * 진열대(published_curricula)의 문서 하나. 커리큘럼마다 최신 판 하나라 _id 가 커리큘럼 ID 다.
 * 배포가 읽는 문서라 curriculum 칸이 원본 모양이어야 한다(CurriculumNode)
 */
@Document("published_curricula")
public record PublishedCurriculumEntity(@Id String id, int version, Instant publishedAt, CurriculumNode curriculum) {
}
