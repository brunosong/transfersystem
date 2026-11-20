# 저작 서비스 시스템 설계

> [저작 도메인 모델](../domain/authoring-model.md)을 Mongo 위에 어떻게 올리는지 정한다.
> 왜 저작 서비스를 따로 두고 초안과 판을 나눴는지는 [ADR 0008](../adr/0008-authoring-service.md)에 있다.

## 1. 컬렉션

DB는 배포가 지금 읽는 `learningDB`를 그대로 쓴다.

| 컬렉션 | 내용 | 인덱스 |
|---|---|---|
| `skeleton_drafts` | 뼈대 초안. `_id`는 커리큘럼 ID | 기본 `_id` |
| `subject_drafts` | 과목 초안. `_id`는 과목 ID | `curriculumId` |
| `curriculum_versions` | 모든 판(기록용). 공개할 때마다 하나씩 쌓인다 | `(curriculumId, version)` 유일 |
| `published_curricula` | 커리큘럼마다 가장 최근 판 하나. **배포가 읽는 곳** | `curriculum.curriculumId` 유일 |
| `counters` | 커리큘럼 번호표 | 기본 `_id` |

## 2. 배포는 `published_curricula`만 읽는다

판을 두 곳에 둔다. 기록용(`curriculum_versions`)에는 모두 쌓고, 배포가 읽는 곳(`published_curricula`)에는 커리큘럼마다 최신 판 하나만 둔다.

| | 고른 것: 두 곳 | 고르지 않은 것: 한 곳에 쌓기 |
|---|---|---|
| 배포 코드 | 그대로. 원본 저장소의 컬렉션 이름만 `published_curricula`로 바꾼다 | 배포가 가장 큰 판 번호를 골라 읽도록 고쳐야 한다. 지금 `findOne`은 아무 판이나 집는다 |
| 감수하는 것 | 공개 한 번에 두 곳을 쓴다(3절) | |

`published_curricula`의 문서 모양은 지금 원본 문서 모양과 같다([원본 읽기 4.1](source-read.md#41-원본-문서의-모양)). 배포의 원본 변환기가 그대로 읽는다.

## 3. 공개는 트랜잭션 하나로 한다

공개는 `curriculum_versions`에 판을 넣고 `published_curricula`를 바꾼다. 둘 사이에 서버가 죽으면 판은 생겼는데 배포는 옛 판을 읽는다. 그래서 **Mongo 트랜잭션으로 두 쓰기를 묶는다.**

트랜잭션은 레플리카 셋에서만 된다. 로컬과 k8s의 Mongo를 **단일 노드 레플리카 셋**으로 바꾼다(`--replSet rs0`, 처음 한 번 `rs.initiate()`). 접속 주소에 레플리카 셋 이름을 붙여야 하는지는 바꿀 때 배포 서비스로 확인한다.

초안을 읽는 쪽은 트랜잭션에 넣지 않아도 된다. 초안을 하나씩 읽을 때 내용과 revision이 함께 오고, 그 revision이 공개 요청의 목록과 같으면 그 내용이 곧 공개하는 사람이 본 내용이다.

```text
1. 뼈대 초안과 과목 초안을 모두 읽는다
2. revision 목록이 요청과 다르면 409 (도메인 모델 5절)
3. 트랜잭션 시작
     curriculum_versions 에 판 넣기 (판 번호 = 직전 + 1)
     published_curricula 를 이 판으로 바꾸기
   트랜잭션 끝
```

둘이 동시에 같은 커리큘럼을 공개하면 둘 다 같은 판 번호를 만든다. `(curriculumId, version)` 유일 인덱스가 늦은 쪽을 막고, 늦은 쪽은 409를 받는다.

## 4. 낙관적 락은 조건부 갱신으로 직접 쓴다

```text
updateOne(
  { _id: "SUB-…", revision: 5 },                 ← 내가 본 revision일 때만
  { $set: { 내용 }, $inc: { revision: 1 } }
)
고친 문서가 0개 → 누가 먼저 고쳤다 → 409
```

Spring Data의 `@Version`도 같은 일을 하지만 쓰지 않는다. 비교와 거절이 코드에 보이지 않는다.

## 5. ID

| 무엇 | 방식 | 예 |
|---|---|---|
| 커리큘럼 | 번호표로 순서대로 매긴다. 사람이 읽기 쉽게 | `CUR001` |
| 학년, 학기, 과목, 단원, 차시 | 종류 접두어 + UUID. 도메인이 만든다(Spring을 쓰지 않으므로 Java `UUID`) | `SUB-3f2a9c1e-…` |

커리큘럼 번호는 `counters` 문서 하나를 `findOneAndUpdate`의 `$inc`로 1 올려 가져온다. 이 갱신은 문서 하나에 대한 한 번의 쓰기라, 둘이 동시에 만들어도 번호가 겹치지 않는다. 사람이 치게 하면 같은 번호를 두 번 칠 수 있어서 고르지 않았다.

## 6. 문서 크기

Mongo 문서 하나는 16MB가 한계다. 초안은 과목마다 나뉘어 작지만, 판은 커리큘럼 하나가 문서 하나다.

- **지금은 한계를 받아들이고, 공개할 때 16MB를 넘으면 422로 거절한다.** 지금 시드 문서는 약 10MB다.
- 실제로 넘는 일이 생기면 판을 여러 문서로 나누고 원본 변환기를 함께 고친다([ADR 0008](../adr/0008-authoring-service.md) 원칙 2).
- 판을 계속 쌓으므로 `curriculum_versions`가 커진다. 오래된 판 지우기는 나중에 정한다.

## 7. 바꿔야 할 것

| 무엇 | 바꿀 것 |
|---|---|
| 로컬 Mongo (`infrastructure/docker-compose/mongodb.yml`) | 단일 노드 레플리카 셋 |
| k8s Mongo (`infrastructure/k8s/mongodb`) | 같다 |
| 배포의 원본 저장소 데이터 | 컬렉션을 `published_curricula`로. 코드는 고치지 않는다 |
| 시드 | 시드 생성기(`LearningMaterialDataGenerator`) 대신 저작 서비스 API로 넣는다. 원본 저장소를 바꾸면 옛 `CurriculumData`의 커리큘럼은 배포 화면에서 보이지 않는다 |
