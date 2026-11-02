# Transfer System 완성 로드맵

> 목적: 학습·포트폴리오용. 범위: 로컬 E2E 검증 + Kubernetes 배포까지.
> 최종 목표: 커리큘럼 하나를 **온라인캠퍼스와 부트캠프 두 대상에 모두 반영하는 사가**를
> transfer-service가 **오케스트레이션**하고, 로컬 E2E로 검증한 뒤 k8s에 배포한다.

## 사가 설계

### 흐름
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

### 핵심 설계 결정
1. **before-image와 inbox는 대상 MySQL에 같이 저장한다.** 스냅샷을 Postgres에 두면 MySQL 쓰기와 원자적일 수 없다. 그래서 대상 DB마다 `migration_snapshot`과 `migration_inbox`를 두고, 업서트와 한 트랜잭션으로 쓴다.
2. **참여자 응답은 아웃박스 대신 "멱등 재응답"으로 신뢰성을 확보한다.** inbox에 처리 결과를 저장한다. 응답이 유실되면 오케스트레이터가 타임아웃 후 명령을 재전송하고, 참여자는 저장된 결과를 다시 응답한다.
3. **Kafka 키는 sagaId로 한다.** 같은 사가의 APPLY와 COMPENSATE 순서가 같은 파티션 안에서 보장된다.
4. **시맨틱 락을 쓴다.** 같은 커리큘럼으로 진행 중인 사가가 있으면 새 전송을 거절(409)한다.

### 메시지 계약
- `MigrationCommand`: sagaId, transferId, commandType(APPLY/COMPENSATE), targetSystem, chunkOffset, totalChunks, payload
- `MigrationReply`: sagaId, transferId, commandType, targetSystem, chunkOffset, result(SUCCEEDED/FAILED/COMPENSATED), message

### 사가 완료 기준
| 항목 | 해야 할 것 |
|---|---|
| 사가 상태 영속화 | `transfer_saga` 테이블(현재 단계, 상태, version)과 오케스트레이터의 상태 전이 |
| 상관관계 ID | 전송당 sagaId 하나, 요청과 응답 모두에 포함 |
| 참여자 멱등성 | inbox 테이블로 중복 명령 무시하고 저장된 결과로 재응답 |
| 보상 | 멱등하고 재시도 가능한 before-image 복원 |
| 타임아웃 | deadline 초과 시 재전송, 그래도 안 되면 보상 |
| 동시성 | 낙관적 락(`@Version`), 아웃박스 중복 발행 방지, 시맨틱 락 |
| 관측 가능성 | `GET /transfers/{id}`로 사가 진행 상태 조회 |

---

## Phase 0. 기반 정리 ✅
*목표: 빌드가 깨끗하고 코드에 잡음이 없는 상태.*

- [x] jib 베이스 이미지를 `eclipse-temurin:21-jre`로 교체하고 설정을 루트 `pluginManagement`로 모음. `install` 때마다 Docker Hub로 push하던 바인딩 제거
- [x] 데드 코드 삭제
  - 예전 동기 전송 경로
  - API 전송 경로(`transfer-infrastructure` 모듈, Feign)
  - `data-migration-application` 모듈(`CreateLearningMeta` 스텁, AI 서비스 잔재)
  - 사가 잔재(`DataMigrationSaga`, `TransferSagaHelper`). Phase 3에서 새로 설계
  - 빈 테스트, `study-api`
- [x] 패키지명 통일
  - `transfer.messaging` → `transfer.service.messaging`
  - `system.main` → `transfer.service`(메인 클래스 `TransferServiceApplication`)
  - `meterials` → `materials`
- [x] 중복 pom 설정 정리. Avro 코드 생성 설정을 루트로 모으고, A/B/C 설명은 [ADR 0001](adr/0001-message-contract-distribution.md)로 이동

> 남겨 둔 것: `TransType.API` 값은 DB 컬럼에 쓰이므로 Phase 2의 Flyway 도입 때 함께 정리한다. Mongo 시드 생성기(`*DataGenerator`)는 Phase 2에서 로컬 시드 방식으로 옮긴다.

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

- [ ] docker-compose 통합(Postgres, MySQL, Mongo, Kafka, Schema Registry, Redis, Kafka UI)
- [ ] MySQL `init.sql`의 MySQL 8 문법 오류 수정
- [ ] Flyway 도입, `DROP` + `sql.init.mode: always` 제거
- [ ] local 전용 설정을 `application.yml`로 옮기고 환경별 값은 `${ENV:default}`로
- [ ] Avro subject 파일명 불일치 수정, `SchemaRegistryInitializer` 정리
- [ ] Testcontainers 기반 `contextLoads`

## Phase 3. 사가 구현
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

## Phase 4. 테스트
- [ ] 오케스트레이터 상태 기계 단위 테스트
- [ ] 서비스별 Testcontainers 통합 테스트
- [ ] E2E 시나리오: 정상 / 부트캠프 실패 후 복원 / 중복 전달 / 응답 유실 후 재전송 / 동시 요청 409 / 보상 실패
- [ ] GitHub Actions: PR마다 빌드와 테스트

## Phase 5. k8s 배포
- [ ] Kustomize `base/` + `overlays/dev` 구조로 두 서비스 매니페스트
- [ ] Actuator 기반 liveness/readiness probe
- [ ] 비밀번호를 Secret으로 옮기고 커밋된 비밀번호는 교체
- [ ] Redis, MySQL, Schema Registry 매니페스트 정비 (Kafka는 Strimzi 고려)
- [ ] GitHub Actions → jib push → Argo CD Image Updater
- [ ] Argo Application을 GitHub repo 기준으로 두 서비스 등록, Bitbucket/Jenkins 참조 제거

## Phase 6. 포트폴리오 마감
- [ ] README: 아키텍처 다이어그램, 사가 시퀀스 다이어그램, 실행 방법
- [ ] ADR: 오케스트레이션 선택, 멱등 재응답 선택, before-image 위치
- [ ] (선택) OpenTelemetry로 sagaId 단위 트레이싱

---

## 미정 사항 (Phase 5 전까지 결정)
1. k8s 클러스터: 로컬(kind, k3d, minikube)인지 클라우드인지
2. config-service 유지 여부 (제안: 제거하고 ConfigMap/Secret 사용)
3. Secret 관리 방식 (제안: Sealed Secrets)
