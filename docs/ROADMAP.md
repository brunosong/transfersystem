# Transfer System 완성 로드맵

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

## 화면 (transfer-console) ✅
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
  - 개발 서버·nginx 가 `/api/v1/targets` 를 data-migration-service 로 넘긴다(`DATA_MIGRATION_SERVICE_URL`)
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
- [ ] transfer-service 버그: `totalChunkSize`가 0으로 저장됨, admin id 하드코딩, `Optional.get()`, `DynamicDataSourceConnector`의 `null` 반환, 요청마다 `MongoClient` 생성

## Phase 2. 로컬 실행 환경
*목표: `git clone` → `docker compose up` → 두 서비스 기동 → `curl` 한 번으로 이관 성공.*

- [ ] docker-compose 통합(Postgres, MySQL, Mongo, Kafka, Schema Registry, Kafka UI)
- [ ] MySQL `init.sql`의 MySQL 8 문법 오류 수정
- [x] Flyway 도입, `DROP` + `sql.init.mode: always` 제거 (두 서비스, 재시작 후 데이터 유지 확인)
- [ ] local 전용 설정을 `application.yml`로 옮기고 환경별 값은 `${ENV:default}`로
- [ ] Avro subject 파일명 불일치 수정, `SchemaRegistryInitializer` 정리
- [ ] Testcontainers 기반 `contextLoads` (Postgres 어댑터 테스트는 화면 작업에서 먼저 도입)
- [ ] 조회 어댑터 테스트용 스키마(`transfer-query-schema.sql`)를 Flyway 마이그레이션으로 대체

## Phase 3. 배포 모델 구현
*목표: [도메인 모델](domain/model.md)대로 배포가 끝났는지 판단한다. 지금은 0번 청크의 결과가 배포 결과가 된다.*

- [ ] 메시지 계약 ([이벤트와 메시지](domain/events.md)): `ApplyChunkCommand`, `ApplyResult`, 시도 번호, 반영 내용, Avro 타입 내용, namespace 통일, 호환성 `FULL`
- [ ] 원본에서 지운 과목 빼기 ([이벤트와 메시지](domain/events.md#6-원본에서-지운-과목은-대상에서도-뺀다))
- [ ] 이름 바꾸기: `Transfer` → `Deployment`, 서비스 이름, 패키지, 토픽 ([용어집](domain/glossary.md), [컨텍스트 맵](domain/context-map.md))
- [ ] `Deployment` 애그리거트: 상태 기계(`IN_PROGRESS`, `SUCCEEDED`, `FAILED`), 청크 상태와 시도 번호, 결과 처리 규칙, `@Version`
- [ ] 뼈대 성공 후 과목 발행 (불변식 3), 청크 내용 보관 (불변식 4)
- [ ] 명령 키를 대상 + 커리큘럼 ID로, 결과 키를 배포 ID로. 대상 쪽 `Thread.sleep(1000)` 제거
- [ ] 아웃박스 고치기 ([ADR 0004](adr/0004-transactional-outbox.md)): 순번과 키 저장, 행마다 브로커 응답 확인, 일시적 실패는 다음 주기에 재발행, 내용 대신 참조 저장, 사가 칸 제거, 어드바이저리 락, 발행 주기 단축, 오래된 행 정리
- [ ] 응답 기한 스케줄러 (`NO_REPLY`), 기한 10분은 재서 고치기
- [ ] 메시지 크기: 1MB 제한, 접수할 때 900KB 초과 거절, `lz4` ([ADR 0005](adr/0005-chunks-on-kafka.md))
- [ ] 오류 처리 나누기: 결과 소비자는 일시적 오류에서 계속 재시도, 대상은 재시도 후 실패 결과 ([시스템 설계](system/architecture.md#7-일이-잘못됐을-때))
- [ ] 청크 내용 정리 작업, 아웃박스 행 정리
- [ ] Redis 모듈 삭제 (`transfer-cache`, `data-migration-cache`)
- [ ] 규칙 A(진행 중 배포 하나, 부분 유니크 인덱스), 규칙 B(최근 배포만 재전송), 규칙 C(배포 허용)
- [ ] 대상의 멱등한 반영과 오류 구분 ([대상 규칙](domain/events.md#5-대상이-지켜야-할-규칙)): 잠깐의 오류는 그 자리에서 재시도하고 끝내 안 되면 실패 결과, 해석할 수 없는 명령은 DLT
- [ ] 사가 흔적 정리: `sagaId`, `SagaStatus`, `infrastructure/saga`

## Phase 4. 테스트
- [ ] 배포 애그리거트 상태 기계 단위 테스트
- [ ] 서비스별 Testcontainers 통합 테스트
- [ ] E2E 시나리오: 정상 / 뼈대 실패 후 재전송 / 과목 실패 후 재전송 / 중복 전달 / 응답 유실 후 재전송 / 동시 요청 409 / 오래된 배포 재전송 거절
- [ ] GitHub Actions: PR마다 빌드와 테스트 (Maven + console 의 `npm run typecheck`, `npm test`)

## Phase 5. k8s 배포
- [ ] Kustomize `base/` + `overlays/dev` 구조로 두 서비스와 transfer-console 매니페스트 (console 은 `TRANSFER_SERVICE_URL`, `DATA_MIGRATION_SERVICE_URL` 에 FQDN 사용)
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
POST /transfers
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
- `MigrationCommand`: sagaId, transferId, commandType(APPLY/COMPENSATE), targetSystem, chunkOffset, totalChunks, payload
- `MigrationReply`: sagaId, transferId, commandType, targetSystem, chunkOffset, result(SUCCEEDED/FAILED/COMPENSATED), message

#### 사가 완료 기준
| 항목 | 해야 할 것 |
|---|---|
| 사가 상태 영속화 | `transfer_saga` 테이블(현재 단계, 상태, version)과 오케스트레이터의 상태 전이 |
| 상관관계 ID | 전송당 sagaId 하나, 요청과 응답 모두에 포함 |
| 참여자 멱등성 | inbox 테이블로 중복 명령 무시하고 저장된 결과로 재응답 |
| 보상 | 멱등하고 재시도 가능한 before-image 복원 |
| 타임아웃 | deadline 초과 시 재전송, 그래도 안 되면 보상 |
| 동시성 | 낙관적 락(`@Version`), 아웃박스 중복 발행 방지, 시맨틱 락 |
| 관측 가능성 | `GET /transfers/{id}`로 사가 진행 상태 조회 |

### 사가 구현 목록
**3-1. 계약**
- [ ] `MigrationCommand`/`MigrationReply` 스키마 추가, Avro namespace 통일

**3-2. 오케스트레이터 (transfer-service)**
- [ ] `transfer_saga`, `saga_chunk_ack` 테이블
- [ ] `TransferSagaOrchestrator`: 순수 도메인 상태 전이. 응답 기록 → 단계 완료 판단 → 다음 명령을 아웃박스에 저장까지 한 트랜잭션
- [ ] 아웃박스 개선: 발행 중 상태 또는 `SKIP LOCKED`, 동기 send, 완료된 행 정리
- [ ] 타임아웃 스케줄러, 시맨틱 락, `GET /transfers/{id}`

**3-3. 참여자 (data-migration)**
- [ ] 대상 DB마다 `migration_inbox`, `migration_snapshot`
- [ ] APPLY: inbox 확인 → before-image 저장 + 업서트 + inbox 기록(한 트랜잭션) → 응답
- [ ] COMPENSATE: 스냅샷으로 복원(새로 생성된 데이터면 삭제), 멱등
- [ ] 에러 분류: 일시적 오류는 재시도 후 DLT, 비즈니스 오류는 FAILED 응답
