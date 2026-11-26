# Deployment System 완성 로드맵

> 목적: 학습·포트폴리오용. 범위: 로컬 E2E 검증 + Kubernetes 배포까지.
> 최종 목표: 운영자가 커리큘럼을 골라 대상에 **배포하고 들어간 것까지 확인하는 시스템**을 로컬 E2E로 검증한 뒤 k8s에 배포한다.
> 대상 반영은 나중에 서비스 팀 소유의 별도 서비스로 떼어 낸다([ADR 0002](adr/0002-target-agent.md)).
> 사가는 뒤로 미뤘다([ADR 0003](adr/0003-defer-saga.md)). 설계 내용은 맨 아래 "나중에"에 있다.

## Phase 0. 기반 정리 ✅
*목표: 빌드가 깨끗하고 코드에 잡음이 없는 상태.*

- [x] jib 베이스 이미지를 `eclipse-temurin:21-jre`로 교체하고 설정을 루트 `pluginManagement`로 모음. `install` 때마다 Docker Hub로 push하던 바인딩 제거
- [x] 데드 코드 삭제
  - 예전 동기 전송 경로
  - API 전송 경로(`transfer-infrastructure` 모듈, Feign)
  - `data-migration-application` 모듈(`CreateLearningMeta` 스텁, AI 서비스 잔재)
  - 사가 잔재(`DataMigrationSaga`, `TransferSagaHelper`)
  - 빈 테스트, `study-api`
- [x] 패키지명 통일
  - `transfer.messaging` → `transfer.service.messaging`
  - `system.main` → `transfer.service`(메인 클래스 `TransferServiceApplication`)
  - `meterials` → `materials`
- [x] 중복 pom 설정 정리. Avro 코드 생성 설정을 루트로 모으고, A/B/C 설명은 [ADR 0001](adr/0001-message-contract-distribution.md)로 이동

> 남겨 둔 것: `TransType.API` 값은 DB 컬럼에 쓰이므로 Phase 2의 Flyway 도입 때 함께 정리한다. Mongo 시드 생성기(`*DataGenerator`)는 Phase 2에서 로컬 시드 방식으로 옮긴다.

## 화면 (deployment-console) ✅
*목표: 전송을 요청하고, 사가가 어디까지 진행됐는지 눈으로 확인한다.*

- [x] 조회 API: 목록(상태 필터·페이지), 상태별 집계, 상세(사가 단계 모양), 원본 설정 목록
- [x] 전송 접수를 `POST /api/v1/transfers`(202 + Location)로 바꾸고, 응답에 transferId 를 담음(전에는 `null`)
- [x] `@Valid` 가 `BindingResult` 때문에 무시되던 문제 수정, 404/400/422 오류 응답 정리
- [x] 조회 어댑터를 Testcontainers Postgres 로 검증(Testcontainers 1.21.4, Docker Engine 29 호환)
- [x] React + Vite + TS 화면: 전송 현황, 전송 상세(사가 타임라인·청크 칸), 새 전송
- [x] 모의 데이터 모드: 사가 완성 후의 흐름(두 대상 반영, 실패 시 역순 보상)을 시간에 따라 흉내 냄
- [x] nginx 컨테이너 이미지(`/api` 프록시, 요청 시점 DNS 조회)

> 지금은 단계가 `APPLY` 하나이고 대상 이름 대신 `targetId` 만 내려 주므로 화면이 시드 표로 이름을 붙인다.

## 화면 다시 기획: 콘텐츠 배포 콘솔
*목표: 실제 서비스처럼 설정하고, 고르고, 보내고, 대상에 들어간 것까지 확인한다. 기획은 [console-plan.md](console-plan.md).*

- [x] 화면 기획 문서 (화면 7개, 필요한 API, 하지 않는 것)
- [x] [화면 설계 문서](ui/screens.md) (원칙, 화면별 결정, 상태 표현)
- [ ] 화면 고치기: [모델과 어긋나는 점](domain/model.md#8-지금-화면모의와-어긋나는-점), [화면 설계의 고칠 목록](ui/screens.md#6-지금-화면에서-고칠-것)
- [x] 1단계: 모의 모드로 화면 7개 (대시보드, 원본 저장소, 대상 시스템, 콘텐츠 탐색, 배포 요청, 배포 이력·상세, 대상 데이터 확인)
  - 이전 화면의 사가 표시(단계 타임라인, 보상 시뮬레이션)는 뺐다.
  - 개발 서버·nginx 가 `/api/v1/targets` 를 target-agent 로 넘긴다(`TARGET_AGENT_URL`)
- [ ] 2단계: 백엔드 API (원본 조회 → 대상 목록 → 배포 요청 보강 → 원본 CRUD → 대상 데이터 조회 → 재전송)
- [ ] 3단계: 로컬 E2E로 처음부터 끝까지 한 번 돌리기 (Phase 2 환경 필요)

## 도메인 설계 문서
*목표: 용어, 경계, 모델을 먼저 정하고 코드를 그에 맞춘다. 문서는 `docs/domain/`.*

- [x] 용어집 (배포 Deployment, 원본 저장소, 대상, 청크, 반영, 검증 …)
- [x] 컨텍스트 맵 (배포·원본·대상 에이전트, 대상 연동 계약)
- [x] [ADR 0002](adr/0002-target-agent.md) 대상 반영을 떼어 낼 수 있는 대상 에이전트로 둔다
- [x] [ADR 0003](adr/0003-defer-saga.md) 사가 구현을 뒤로 미룬다
- [x] [ADR 0004](adr/0004-transactional-outbox.md) 명령 발행에 별도 아웃박스 테이블을 쓴다
- [x] 모델 (애그리거트, 불변식, 상태 기계)
- [x] 이벤트와 메시지 (대상 연동 계약의 명령·결과·조회)

## 시스템 설계 문서
- [x] [시스템 설계](system/architecture.md) (구성, 트랜잭션 경계, 토픽, 값, 저장소, 장애, 관측, 배포 구성)
- [x] [ADR 0005](adr/0005-chunks-on-kafka.md) 청크 내용을 Kafka 메시지에 직접 싣고 크기를 제한한다
- [x] [원본을 읽어 청크로 나누는 흐름](system/source-read.md) (요청 → 원본 설정 → 문서 꺼내기 → 청크 나누기, 객체 모양, 실패 응답, 남은 문제)
- [x] [ADR 0006](adr/0006-chunk-content-contract.md) 청크 내용 계약은 계층을 타입으로 두고 세부 속성은 JSON 문자열로 보낸다
- [x] [ADR 0007](adr/0007-independent-chunks.md) (기각) 청크끼리 의존을 없애고 과목 청크가 자기 상위를 싣는다. 실제 학년은 속성이 많아 뼈대 먼저(불변식 3)를 유지한다

## 저작 서비스 (authoring-service)
*목표: 콘텐츠팀 대신 커리큘럼을 만들고 고친다. 초안과 공개본을 나눠 고치는 중인 커리큘럼이 배포되지 않게 하고, 동시 수정에서 수정이 사라지지 않게 한다([ADR 0008](adr/0008-authoring-service.md)).*

- [x] [ADR 0008](adr/0008-authoring-service.md) 저작 시스템을 만들고, 초안과 공개본을 나눈다 (2026-11-20)
- [x] [ADR 0009](adr/0009-split-curriculum-draft.md) 커리큘럼 초안을 뼈대와 과목, 두 애그리거트로 나눈다 (2026-11-23)
- [x] 컨텍스트 맵, 용어집에 저작 컨텍스트와 초안·공개·판을 넣는다 (2026-11-20)
- [x] [도메인 설계](domain/authoring-model.md): 뼈대 초안 + 과목 초안 + 판, 불변식 6개, 저장과 공개에 낙관적 락 (2026-11-20)
- [x] [시스템 설계](system/authoring.md): 판은 기록용과 배포가 읽는 곳 두 곳, 공개는 트랜잭션(단일 노드 레플리카 셋), 낙관적 락은 조건부 갱신, 커리큘럼 ID는 번호표 (2026-11-20)
- [ ] Mongo를 단일 노드 레플리카 셋으로: ~~로컬~~(2026-11-20, `mongodb.yml`. 트랜잭션 커밋·롤백 확인), k8s
- [x] 모듈 뼈대 (`authoring-container`, `authoring-domain`, `authoring-dataaccess`, `authoring-application`). 로컬 8002에서 뜨고 레플리카 셋 Mongo에 붙는 것 확인 (2026-11-20)
- [x] 공개: `POST /api/v1/curricula/{id}/publish`, 판 기록 `GET .../versions`. 판을 창고(`curriculum_versions`)와 진열대(`published_curricula`)에 트랜잭션 하나로 쓴다. 화면의 [공개]와 판 기록 탭 (2026-11-24)
- [x] 초안 API (~~목록~~ `GET /api/v1/curricula?q=&page=&size=` 2026-11-21, ~~만들기~~ `POST /api/v1/curricula` 2026-11-21, ~~읽기~~ `GET /api/v1/curricula/{id}` 2026-11-21, ~~고치기~~ `PUT /api/v1/curricula/{id}` 뼈대의 제목·설명, 낙관적 락 409 2026-11-21, ~~학년 추가~~ `POST /api/v1/curricula/{id}/grades` 2026-11-21, ~~학기 추가~~ `POST .../grades/{gradeId}/semesters` 2026-11-21, ~~학년 수정~~ `PUT .../grades/{gradeId}`, ~~학기 수정~~ `PUT .../semesters/{semesterId}` 2026-11-21, ~~학년 삭제~~ `DELETE .../grades/{gradeId}?revision=` 학기도 함께 2026-11-22, 학기 삭제, ~~과목 만들기·목록~~ `POST/GET .../subjects` 따로 저장하는 애그리거트, 뼈대 revision 은 그대로 2026-11-22, ~~단원 추가~~ `POST .../subjects/{subjectId}/units` 과목 revision 만 오름 2026-11-23, ~~과목 수정~~ `PUT .../subjects/{subjectId}` 2026-11-23, ~~차시 추가~~ `POST .../units/{unitId}/lessons` 2026-11-23, ~~단원·차시 수정~~ `PUT .../units/{unitId}`, `PUT .../lessons/{lessonId}` 2026-11-23, ~~학기·과목·단원·차시 삭제~~ 과목이 남은 학기·학년은 422 2026-11-24, ~~차시의 세부 칸~~ `learningLevel`만 두고 `lessonNumber`는 공개할 때 매긴다. `format`은 배포가 읽지 않아 넣지 않는다 2026-11-26)와 공개 API
- [ ] 배포의 원본 저장소가 공개본 컬렉션을 가리키게 한다: ~~로컬~~(2026-11-26, seed `V15`. 저작에서 공개한 커리큘럼이 대상까지 반영되는 것 확인), ~~k8s~~(2026-11-26, dev seed `V15`, 배포 계정 역할에 `published_curricula` 읽기만. 초기화 스크립트라 이미 뜬 Mongo에는 `grantPrivilegesToRole`을 한 번 직접 돌려야 한다. 클러스터에서는 아직 확인 못 함). ~~시드 생성기 대신 저작 서비스로 시드를 넣는다~~(2026-11-26, `infrastructure/seed/seed-curricula.mjs`. 커리큘럼 5개, 하나에 과목 8·차시 48. 옛 생성기와 `CurriculumData`는 큰 문서 측정용으로 남긴다)
- [x] 순서: 커리큘럼·과목은 정렬 순서 숫자, 학년·학기·단원·차시는 목록 자리를 위·아래로 옮긴다 ([저작 모델 6.1](domain/authoring-model.md#61-순서), 2026-11-24)
- [ ] 커리큘럼 정렬 순서를 대상까지 보낸다: 청크 계약(뼈대 내용)과 대상 DB에 칸 추가 ([ADR 0006](adr/0006-chunk-content-contract.md))
- [ ] 화면: `deployment-console`에 저작 메뉴. ~~서비스 탭~~, ~~목록·상세 화면~~(2026-11-21, [화면 설계](ui/screens.md#저작-커리큘럼-목록과-상세-2026-11-21). 만들기, 제목·설명, 학년·학기 추가, 409 안내가 돈다. 개발 서버만 저작 서비스로 넘긴다). ~~과목 상세 화면~~(`/authoring/{id}/subjects/{subjectId}`, 단원 추가). 남은 것: 공개·과목·판 기록 기능, nginx 경로, 모의 모드

## 대상 에이전트 떼어 내기 ([ADR 0002](adr/0002-target-agent.md) 이행 계획)
*목표: 서비스 DB 접속 정보를 배포 시스템에서 없애고, 마지막에는 에이전트를 서비스 팀 소유의 별도 서비스로 떼어 낸다.*

- [ ] 2단계 계약 분리: 대상 목록을 배포 컨텍스트로 옮김, 계약(명령·결과·조회) 구현, 결과에 반영 내용 담기, [대상별 토픽](system/architecture.md#4-kafka-토픽)으로 나누기. 배포 시스템이 바뀌는 마지막 단계
- [ ] 3단계 대상별 에이전트: 대상 시스템마다 따로 띄우고 DB 계정 권한을 최소로
- [ ] 4단계 떼어 내기: 각 서비스 팀 소유의 컨슈머 서비스로 옮기고, 3·4단계에서 배포 시스템 코드가 바뀌지 않았는지 확인

## Phase 1. 정확성 버그 수정
*목표: 데이터가 맞는 DB에, 맞는 트랜잭션으로 써짐.*

- [ ] `OnlineCampusDataSourceConfig`: DEV 대신 라우팅 DataSource를 반환
- [ ] 대상별 트랜잭션 매니저 명시, self-invocation 제거
- [ ] `DataPersistHandler`: 프록시에서도 안전한 어노테이션 조회
- [ ] `H2Dialect` 하드코딩과 `hbm2ddl.auto=update` 제거
- [ ] Bootcamp 저장 실제 구현
- [ ] deployment-service 버그: admin id 하드코딩, `Optional.get()` (`totalChunkSize`가 0으로 저장되던 것은 2026-11-12에 고쳤다)
- [x] 원본 읽기: `DynamicDataSourceConnector`의 `null` 반환, 요청마다 `MongoClient` 생성, MySQL 풀을 쓸 때마다 닫던 문제, Mongo 문서를 드라이버가 바로 JSON으로 씀

## Phase 2. 로컬 실행 환경
*목표: `git clone` → `docker compose up` → 두 서비스 기동 → `curl` 한 번으로 배포 성공.*

- [ ] docker-compose 통합(Postgres, MySQL, Mongo, Kafka, Schema Registry, Kafka UI)
- [ ] MySQL `init.sql`의 MySQL 8 문법 오류 수정
- [x] Flyway 도입, `DROP` + `sql.init.mode: always` 제거 (두 서비스, 재시작 후 데이터 유지 확인)
- [ ] local 전용 설정을 `application.yml`로 옮기고 환경별 값은 `${ENV:default}`로
- [x] Avro subject 파일명 불일치 수정, `SchemaRegistryInitializer` 정리 (지움, `RecordNameStrategy`로 통일)
- [ ] Testcontainers 기반 `contextLoads` (Postgres 어댑터 테스트는 화면 작업에서 먼저 도입)
- [ ] 조회 어댑터 테스트용 스키마(`deployment-query-schema.sql`)를 Flyway 마이그레이션으로 대체
- [ ] 안 쓰는 Mongo 코드 정리: `spring.data.mongodb.uri`와 k8s `MONGODB_*`, `MongoConfiguration`, `LearningMaterialMongoRepository`와 엔티티, 테스트, `MongoDBExamDataGenerator` 삭제, Mongo 자동 설정 끄기. 원본을 읽는 `MongoConnectHelper`와 MongoDB는 유지

## Phase 3. 배포 모델 구현
*목표: [도메인 모델](domain/model.md)대로 배포가 끝났는지 판단한다. 2026-11-15에 정상 흐름(접수 → 뼈대 → 결과 → 과목 → `SUCCEEDED`)이 로컬에서 끝까지 돌았다. 실패 흐름(응답 기한, 재전송, 규칙 A)이 남았다.*

- [x] 메시지 계약 ([이벤트와 메시지](domain/events.md)): `ApplyChunkCommand`, `ApplyResult`, Avro 타입 내용, namespace 통일, 호환성 `FULL`. 시도 번호는 항상 1, 반영 내용은 아직 버림(모델 작업에서)
- [ ] 원본에서 지운 과목 빼기 ([이벤트와 메시지](domain/events.md#6-원본에서-지운-과목은-대상에서도-뺀다))
- [x] 이름 바꾸기 1: 배포 쪽 클래스, 필드, 패키지, REST 경로, DB 이름(`V3`), 화면의 API 타입 ([용어집](domain/glossary.md), [컨텍스트 맵](domain/context-map.md))
- [x] 이름 바꾸기 2: 모듈과 서비스 이름(`transfer-service` → `deployment-service`), 루트 패키지, 설정 키, 이미지, k8s
- [x] 이름 바꾸기 3: `data-migration-service` → `target-agent`, `DataMigration*` 코드 이름, 표 `targets`
- [ ] 토픽 이름(`data-migration-request`, `data-migration-response`)은 아웃박스를 바꿀 때 대상별 토픽으로 함께 바꾼다
- [x] `Deployment` 애그리거트: 상태 기계(`IN_PROGRESS`, `SUCCEEDED`, `FAILED`), 청크 상태와 시도 번호, 결과 처리 규칙, `@Version`. 2026-11-15에 접수(`accept`)와 결과 처리(`recordSuccess`/`recordFailure`)에 연결했다. 새 배포는 `Deployment.create`로만 만든다
- [x] `SourceContentData`를 `SourceDocument`(꺼낸 문서), `Curriculum`(트리 값 객체), 청크로 나누고, 청크로 나누는 일을 배포로 옮기기 ([원본 읽기 7장](system/source-read.md#7-원본-문서-커리큘럼-청크를-나눈다)). `totalChunkSize` 0 버그도 함께 풀린다
- [ ] 뼈대 성공 후 과목 발행 (불변식 3), 청크 내용 보관 (불변식 4). 지금 접수 코드는 뼈대와 과목을 한꺼번에 아웃박스에 넣는다. 이를 대신할 방식은 [ADR 0007](adr/0007-independent-chunks.md)에서 검토하고 기각했다. 내용을 어디에 둘지는 A'(내용 표 + 아웃박스는 당분간 전체)로 시작해 2026-11-17에 A(아웃박스는 참조만)로 옮겼다([시스템 설계 6장](system/architecture.md#청크-내용은-어디에-두나))
  - [x] 1. 결정 기록
  - [x] 2. 내용 표 만들기 (`V9` `deployment_chunk_payload`, 포트, 어댑터, `jsonb`)
  - [x] 3. 접수 때 모든 청크 내용 저장 (아웃박스에는 아직 전부 넣음)
  - [x] 4. 결과 처리의 새 길 준비. 먼저 4-1 ~~배포에 커리큘럼 ID 두기~~(V10), 4-2 ~~결과 메시지에 시도 번호 싣기~~. 그다음 (`recordSuccess`/`recordFailure` → 저장 → 돌려받은 청크를 내용 표에서 읽어 아웃박스로). 5에서 연결했다
  - [x] 5-1. 접수는 `Deployment.accept()` + 뼈대만 아웃박스 (2026-11-13. 과목은 아직 나가지 않는다)
  - [x] 5. 전환 (2026-11-15. 결과 리스너가 `DeploymentResultHandler`를 부른다. 옛 결과 표에도 함께 적는다): 접수는 `Deployment.accept()` + 뼈대만 아웃박스, 결과 리스너는 새 길. 옛 표에도 함께 적는다. 로컬에서 실제로 한 번 돌려 본다
  - [x] 6. 옛 길 정리: ~~콘솔 조회를 `deployment_chunk`로 옮기기~~(나-1, 2026-11-16. 청크 상태도 `WAITING`/`SENT`/`APPLIED`/`FAILED`로), ~~`deployment_chunks`, `saveChunk` 삭제~~(나-2, V12), ~~캐시 경로 삭제~~(나-3), ~~결과 메시지의 옛 상태 값 정리~~(나-4, V13. 배포 상태는 `IN_PROGRESS`/`SUCCEEDED`/`FAILED`만, 결과는 `applied` 하나)
- [ ] ~~명령 키를 대상 + 커리큘럼 ID로~~(V7), ~~결과 키를 배포 ID로~~(target-agent가 이미 배포 ID로 보낸다). 남은 것: 대상 쪽 `Thread.sleep(1000)` 제거
- [x] 아웃박스 고치기 ([ADR 0004](adr/0004-transactional-outbox.md)): ~~순번 저장~~(V11 `seq`, 순번대로 한 번에 100개씩 읽기), ~~키 저장~~(V7 `message_key`, `시스템:환경:커리큘럼 ID`), ~~행마다 브로커 응답 확인, 일시적 실패는 다음 주기에 재발행~~(같은 키의 뒤 행은 기다림), ~~내용 대신 참조 저장~~(2026-11-17. 행에는 배포 ID, 청크 번호, 시도와 대상 이름 같은 작은 값만, 발행할 때 내용 표에서 읽음), ~~사가 칸 제거~~(V6에서 `saga_id`, `saga_status` 지움), ~~쓰지 않는 `version` 칸 지우기~~(V14), ~~어드바이저리 락~~(세션 락, `PublishLock`), ~~발행 주기 단축~~(코드 기본값 1초, 로컬은 천천히 보려고 30초), ~~오래된 행 정리~~(2026-11-17, `CleanupScheduler`), ~~프로듀서 `max.block.ms` 줄이기~~(로컬 10초. dev는 config-service에서 맞춘다. 브로커가 죽으면 `send()`가 60초까지 막혔다, [시스템 설계 10장](system/architecture.md#10-재-봐야-할-것))
- [ ] 가. 실패 흐름. 결과가 오지 않거나 실패로 와도 배포가 끝나고, 운영자가 이어서 보낼 수 있게 한다
  - [ ] 응답 기한 스케줄러 (`NO_REPLY`), 기한 10분은 재서 고치기
    - [x] 가-1. 도메인: `Deployment.expireOverdue(now, 기한)` (2026-11-15)
    - [x] 가-2. 기한이 지난 `SENT` 청크가 있는 배포 찾기 (V5의 부분 인덱스를 쓰도록 상태를 글자 그대로 적음)
    - [x] 가-3. 스케줄러: 1분마다, 배포마다 따로 처리 (`ResponseDeadlineScheduler`, `DeploymentDeadlineHandler`. 기한 `deployment-service.response-deadline.duration` 기본 10m, 주기 `...check-interval` 기본 60000ms)
    - [x] 가-4. 로컬 확인(2026-11-19): 대상을 끄고 접수하면 기한이 지나 뼈대와 배포가 `FAILED`(`NO_REPLY`)가 된다. 대상을 켜서 늦은 성공이 오면 뼈대만 `APPLIED`가 되고, 배포는 `FAILED`, 과목은 `WAITING`으로 남는다. 과목은 운영자가 재전송해야 나간다([도메인 모델](domain/model.md#재전송과-늦은-결과))
  - [ ] 가-5. 실패 결과 확인: 대상이 실패 결과를 보내면 그 청크와 배포가 `FAILED`가 된다
  - [ ] 가-6. 재전송 API (`POST /api/v1/deployments/{id}/retry`): `FAILED` 청크를 시도 번호 +1로 다시 보낸다. 뼈대가 반영돼 있으면 `WAITING` 과목도 함께 보낸다. 내용은 저장해 둔 청크 내용을 쓴다. 내용이 지워진 배포(성공, 더 최근 배포가 있는 실패)는 재전송을 거절한다(규칙 B)
    - [ ] 화면도 [화면 설계 청크 표](ui/screens.md#청크)대로 맞춘다: 실패를 `REJECTED`(대상이 거절함)와 `NO_REPLY`(응답 없음, 반영됐을 수도 있음)로 나눠 보여 준다(상세 API에 실패 종류를 싣는다). 재전송 중인 칸은 파랑으로 깜박인다(지금은 주황)
  - [ ] 가-7. 규칙 A: 커리큘럼 × 대상마다 진행 중인 배포는 하나 (부분 유니크 인덱스, 어기면 409)
- [ ] 메시지 크기: 1MB 제한, 접수할 때 900KB 초과 거절, `lz4` ([ADR 0005](adr/0005-chunks-on-kafka.md))
- [ ] 청크 내용 계약 바꾸기: 단원과 차시에 `attributes`(JSON 문자열), `learningLevel`을 `attributes`로 ([ADR 0006](adr/0006-chunk-content-contract.md))
- [ ] 오류 처리 나누기: 결과 소비자는 일시적 오류에서 계속 재시도, 대상은 재시도 후 실패 결과 ([시스템 설계](system/architecture.md#7-일이-잘못됐을-때))
- [x] 청크 내용 정리 작업, 아웃박스 행 정리 (2026-11-17. `CleanupScheduler`가 1시간마다. 아웃박스는 처리한 지 7일 지난 `COMPLETED`·`FAILED`, 내용은 `SUCCEEDED` 배포와 더 최근 배포가 있는 `FAILED` 배포의 것)
- [x] Redis 모듈 삭제 (`deployment-cache`, `target-agent-cache`, 2026-11-16 나-3). 포트, 에이전트 `RedisConfig`, 쓰는 곳이 없던 `TargetHandler`, `redis.yml`도 지웠다
- [ ] target-agent: 같은 커리큘럼을 다른 대상에 보내면 중복 오류(`CURRICULUM_ID` 유일 제약 위반)가 난다. 대상마다 다른 DB에 들어가야 하는데 같은 DB를 고르는 것으로 보인다(2026-11-15 로컬, CUR123을 대상 1과 3에)
- [ ] 콘솔 nginx 설정(`nginx/default.conf.template`)도 대상 목록(`/api/v1/targets`)을 deployment-service로 보내게 맞추기. 개발 서버(Vite)는 2026-11-15에 바꿨다
- [ ] 커리큘럼별 배포 이력 조회 (`GET /api/v1/curriculums/{id}/deployments`). 지금은 빈 목록을 돌려준다. 배포 표의 `curriculum_id`(V10)로 대상별 마지막 배포를 모은다
- [ ] 규칙 B(최근 배포만 재전송), 규칙 C(배포 허용). 규칙 A는 가-7
- [ ] 대상의 멱등한 반영과 오류 구분 ([대상 규칙](domain/events.md#5-대상이-지켜야-할-규칙)): 잠깐의 오류는 그 자리에서 재시도하고 끝내 안 되면 실패 결과, 해석할 수 없는 명령은 DLT
- [x] 사가 흔적 정리: `sagaId`, `saga_status`(V6), 아웃박스 `type` 값(V8, `ApplyChunkCommand`), deployment-service의 `saga` 모듈 의존, `infrastructure/saga` 모듈(`SagaStatus`, `SagaStep`, `SagaConstants`)을 지웠다. 사가를 다시 시작하면 새로 만든다([ADR 0003](adr/0003-defer-saga.md))

## Phase 4. 테스트
- [x] 배포 애그리거트 상태 기계 단위 테스트 (`DeploymentTest`, `ChunkTest`)
- [ ] 결과 처리를 실제 Postgres로 확인하는 테스트: 결과 하나를 처리하면 청크 상태와 과목 아웃박스 행이 한 트랜잭션에 저장된다. 2026-11-15의 `saveChunk` 버그(배포 ID가 빔)는 가짜 저장소를 쓴 단위 테스트로는 잡히지 않았다(그 코드는 2026-11-16에 지웠다)
- [ ] 서비스별 Testcontainers 통합 테스트
- [ ] E2E 시나리오: 정상(2026-11-15에 손으로 확인, 자동화는 아직) / 뼈대 실패 후 재전송 / 과목 실패 후 재전송 / 중복 전달 / 응답 유실 후 재전송 / 동시 요청 409 / 오래된 배포 재전송 거절
- [ ] GitHub Actions: PR마다 빌드와 테스트 (Maven + console 의 `npm run typecheck`, `npm test`)

## Phase 5. k8s 배포
- [ ] Kustomize `base/` + `overlays/dev` 구조로 두 서비스와 deployment-console 매니페스트 (console 은 `DEPLOYMENT_SERVICE_URL`, `TARGET_AGENT_URL` 에 FQDN 사용)
- [ ] Actuator 기반 liveness/readiness probe
- [ ] 비밀번호를 Secret으로 옮기고 커밋된 비밀번호는 교체
- [ ] MySQL, Schema Registry 매니페스트 정비 (Kafka는 Strimzi 고려)
- [ ] GitHub Actions → jib push(서비스) / docker build(console) → Argo CD Image Updater
- [ ] Argo Application을 GitHub repo 기준으로 두 서비스 등록, Bitbucket/Jenkins 참조 제거

## Phase 6. 포트폴리오 마감
- [ ] README: 아키텍처 다이어그램, 배포 시퀀스 다이어그램, 실행 방법
- [ ] ADR: 직접 만든 배포 파이프라인, Kafka 비동기, 청크 나누기와 업서트, 화면 결정들
- [ ] (선택) OpenTelemetry로 배포 ID 단위 트레이싱

---

## 미정 사항 (Phase 5 전까지 결정)
1. k8s 클러스터: 로컬(kind, k3d, minikube)인지 클라우드인지
2. config-service 유지 여부 (제안: 제거하고 ConfigMap/Secret 사용)
3. Secret 관리 방식 (제안: Sealed Secrets)

---

## 나중에: 사가 (보류)

다른 일을 모두 마친 뒤 할지 다시 정한다([ADR 0003](adr/0003-defer-saga.md)). 아래는 처음 세운 설계이고, 지금 모델과 다른 부분(Kafka 키, 상태 이름)은 다시 맞춰야 한다.

### 사가 설계

#### 흐름
```
POST /deployments
  └─ 사가 시작 (sagaId 1개, 같은 커리큘럼으로 진행 중인 사가가 있으면 거절)
      Step 1. APPLY → 온라인캠퍼스   (청크 N개, 전부 성공 응답이 오면 다음)
      Step 2. APPLY → 부트캠프       (청크 N개)
      ├─ 전부 성공 → SUCCEEDED
      └─ 실패 또는 타임아웃 → COMPENSATING
            → 부트캠프(부분 반영분)와 온라인캠퍼스에 COMPENSATE 명령
            → before-image로 복원 → COMPENSATED
            → 보상도 계속 실패하면 FAILED (수동 개입 필요)
```

#### 핵심 설계 결정
1. **before-image와 inbox는 대상 MySQL에 같이 저장한다.** 스냅샷을 Postgres에 두면 MySQL 쓰기와 원자적일 수 없다. 그래서 대상 DB마다 `migration_snapshot`과 `migration_inbox`를 두고, 업서트와 한 트랜잭션으로 쓴다.
2. **참여자 응답은 아웃박스 대신 "멱등 재응답"으로 신뢰성을 확보한다.** inbox에 처리 결과를 저장한다. 응답이 유실되면 오케스트레이터가 타임아웃 후 명령을 재전송하고, 참여자는 저장된 결과를 다시 응답한다.
3. **Kafka 키는 sagaId로 한다.** 같은 사가의 APPLY와 COMPENSATE 순서가 같은 파티션 안에서 보장된다.
4. **시맨틱 락을 쓴다.** 같은 커리큘럼으로 진행 중인 사가가 있으면 새 전송을 거절(409)한다.

#### 메시지 계약
- `MigrationCommand`: sagaId, deploymentId, commandType(APPLY/COMPENSATE), targetSystem, chunkOffset, totalChunks, payload
- `MigrationReply`: sagaId, deploymentId, commandType, targetSystem, chunkOffset, result(SUCCEEDED/FAILED/COMPENSATED), message

#### 사가 완료 기준
| 항목 | 해야 할 것 |
|---|---|
| 사가 상태 영속화 | `deployment_saga` 테이블(현재 단계, 상태, version)과 오케스트레이터의 상태 전이 |
| 상관관계 ID | 전송당 sagaId 하나, 요청과 응답 모두에 포함 |
| 참여자 멱등성 | inbox 테이블로 중복 명령 무시하고 저장된 결과로 재응답 |
| 보상 | 멱등하고 재시도 가능한 before-image 복원 |
| 타임아웃 | deadline 초과 시 재전송, 그래도 안 되면 보상 |
| 동시성 | 낙관적 락(`@Version`), 아웃박스 중복 발행 방지, 시맨틱 락 |
| 관측 가능성 | `GET /deployments/{id}`로 사가 진행 상태 조회 |

### 사가 구현 목록
**3-1. 계약**
- [ ] `MigrationCommand`/`MigrationReply` 스키마 추가, Avro namespace 통일

**3-2. 오케스트레이터 (deployment-service)**
- [ ] `deployment_saga`, `saga_chunk_ack` 테이블
- [ ] `DeploymentSagaOrchestrator`: 순수 도메인 상태 전이. 응답 기록 → 단계 완료 판단 → 다음 명령을 아웃박스에 저장까지 한 트랜잭션
- [ ] 아웃박스 개선: 발행 중 상태 또는 `SKIP LOCKED`, 동기 send, 완료된 행 정리
- [ ] 타임아웃 스케줄러, 시맨틱 락, `GET /deployments/{id}`

**3-3. 참여자 (target-agent)**
- [ ] 대상 DB마다 `migration_inbox`, `migration_snapshot`
- [ ] APPLY: inbox 확인 → before-image 저장 + 업서트 + inbox 기록(한 트랜잭션) → 응답
- [ ] COMPENSATE: 스냅샷으로 복원(새로 생성된 데이터면 삭제), 멱등
- [ ] 에러 분류: 일시적 오류는 재시도 후 DLT, 비즈니스 오류는 FAILED 응답
