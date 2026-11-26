package com.brunosong.deployment.system.authoring.service.dataaccess.version.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * 진열대(published_curricula)의 문서 하나. 커리큘럼마다 최신 판 하나라 _id 가 커리큘럼 ID 다.
 * 배포가 읽는 문서라 curriculum 칸이 원본 모양이어야 한다(CurriculumNode)
 * 배포는 _id 가 아니라 curriculum.curriculumId 로 찾으므로 그 칸에 인덱스를 둔다.
 * CurriculumNode 에 @Indexed 를 달면 판이 쌓이는 curriculum_versions 에도 유일 인덱스가 생겨서 여기에 둔다
 */
@Document("published_curricula")
@CompoundIndex(name = "curriculum_id_unique", def = "{'curriculum.curriculumId': 1}", unique = true)
public record PublishedCurriculumEntity(@Id String id, int version, Instant publishedAt, CurriculumNode curriculum) {
}
