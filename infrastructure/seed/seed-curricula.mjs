// 저작 서비스 API로 시드 커리큘럼을 만들고 공개한다. 옛 시드 생성기(LearningMaterialDataGenerator) 대신 쓴다.
// 배포는 공개본(published_curricula)만 읽으므로, 만들기만 하지 않고 공개까지 한다.
// 화면과 흐름을 보기 위한 작은 규모다. 큰 문서 성능은 따로 본다.
//
// 실행: node infrastructure/seed/seed-curricula.mjs [저작 서비스 주소, 기본 http://localhost:8002]

const BASE = (process.argv[2] ?? 'http://localhost:8002') + '/api/v1/curricula';

const CURRICULA = ['초등 교육 과정', '중등 교육 과정', '고등 교육 과정', '성인 기초 과정', '특수 교육 과정'];
const SUBJECTS = ['국어', '수학', '영어', '과학'];
const GRADE_COUNT = 2;
const SEMESTER_COUNT = 2;
const SUBJECT_COUNT = 2;   // 학기마다
const UNIT_COUNT = 2;      // 과목마다
const LESSON_COUNT = 3;    // 단원마다
const SEED_MARK = '[시드]';

async function call(method, path, body) {
  const res = await fetch(BASE + path, {
    method,
    headers: { 'Content-Type': 'application/json' },
    body: body && JSON.stringify(body),
  });
  if (!res.ok) {
    throw new Error(`${method} ${path} → ${res.status} ${await res.text()}`);
  }
  return res.json();
}

async function seedCurriculum(index) {
  const title = `${SEED_MARK} ${CURRICULA[index]}`;
  const created = await call('POST', '', { title, description: `${CURRICULA[index]} 전체 커리큘럼` });
  const id = created.curriculumId;
  let skeletonRevision = created.revision;
  const subjectRevisions = {};

  for (let g = 1; g <= GRADE_COUNT; g++) {
    const grade = await call('POST', `/${id}/grades`, { title: `${g}학년`, description: `${g}학년 과정`, revision: skeletonRevision });
    skeletonRevision = grade.revision;

    for (let s = 1; s <= SEMESTER_COUNT; s++) {
      const semester = await call('POST', `/${id}/grades/${grade.gradeId}/semesters`,
        { title: `${s}학기`, description: `${g}학년 ${s}학기`, revision: skeletonRevision });
      skeletonRevision = semester.revision;

      for (let j = 0; j < SUBJECT_COUNT; j++) {
        const subjectTitle = SUBJECTS[(g + s + j) % SUBJECTS.length];
        const subject = await call('POST', `/${id}/subjects`,
          { semesterId: semester.semesterId, title: subjectTitle, description: `${subjectTitle} 학습` });
        let revision = subject.revision;

        for (let u = 1; u <= UNIT_COUNT; u++) {
          const unit = await call('POST', `/${id}/subjects/${subject.id}/units`,
            { title: `단원 ${u}: ${subjectTitle} 주제 ${u}`, description: `${subjectTitle} 단원 ${u}`, revision });
          revision = unit.revision;

          for (let l = 1; l <= LESSON_COUNT; l++) {
            const lessonTitle = `차시 ${l}: ${subjectTitle} 학습 ${l}`;
            const lesson = await call('POST', `/${id}/subjects/${subject.id}/units/${unit.unitId}/lessons`,
              { title: lessonTitle, description: `${subjectTitle} 차시 ${l} 내용`, revision });
            // 새 차시는 학습 수준 1로 생긴다. 옛 시드처럼 1~3으로 섞으려고 한 번 고친다
            const edited = await call('PUT', `/${id}/subjects/${subject.id}/units/${unit.unitId}/lessons/${lesson.lessonId}`,
              { title: lessonTitle, description: `${subjectTitle} 차시 ${l} 내용`, learningLevel: (l - 1) % 3 + 1, revision: lesson.revision });
            revision = edited.revision;
          }
        }
        subjectRevisions[subject.id] = revision;
      }
    }
  }

  const published = await call('POST', `/${id}/publish`, { skeletonRevision, subjectRevisions });
  console.log(`${id} ${title} → 판 ${published.version}`);
}

// 두 번 돌리면 같은 커리큘럼이 또 생기므로, 시드가 이미 있으면 멈춘다
const existing = await call('GET', `?q=${encodeURIComponent(SEED_MARK)}&size=1`);
if (existing.total > 0) {
  console.log(`이미 시드가 있다(${existing.total}개). 멈춘다`);
  process.exit(0);
}
for (let i = 0; i < CURRICULA.length; i++) {
  await seedCurriculum(i);
}
