# Transfer System 프로젝트

## 개요
`Transfer System`은 원본 저장소(MongoDB, MySQL)의 학습 데이터(커리큘럼)를 대상 시스템(온라인캠퍼스, 부트캠프)으로 이관하는 모듈화된 애플리케이션입니다. 서비스(`transfer-service`, `data-migration-service`, `config-service`)와 공통 모듈(`common`), 공용 인프라 모듈(`infrastructure`)로 구성되어 있으며, Kafka, MongoDB, Redis 등의 인프라를 활용합니다.

서비스 간 통신은 **Transactional Outbox** 패턴과 **Saga** 패턴을 기반으로 하며, 메시지 계약(Avro 스키마)은 별도 모듈로 분리해 서비스들이 아티팩트로 공유합니다.

## 프로젝트 목적
- **데이터 전송**: 원본 데이터를 조회해 청크 단위로 나눠 대상 서비스로 보낸다.
- **데이터 마이그레이션**: 받은 데이터를 대상 시스템 DB에 반영한다.
- **모듈화**: 헥사고날 아키텍처로 도메인과 어댑터를 분리한다.
- **신뢰성 있는 메시징**: Outbox·Saga 패턴으로 DB 변경과 메시지 발행의 일관성을 보장한다.

## 진행 상태
학습·포트폴리오용으로 진행 중인 프로젝트입니다. 목표는 커리큘럼 하나를 두 대상 시스템에 모두 반영하는 **오케스트레이션 사가**를 완성하고 Kubernetes에 배포하는 것입니다.
- 남은 작업과 설계: [`docs/ROADMAP.md`](docs/ROADMAP.md)
- 설계 결정 기록(ADR): [`docs/adr/`](docs/adr/)
- 화면 기획: [`docs/console-plan.md`](docs/console-plan.md)

## 기술 스택
- **언어**: Java 21
- **빌드 도구**: Maven (Maven Wrapper 포함)
- **프레임워크**: Spring Boot 3.3, Spring Cloud Config
- **메시징**: Kafka, Schema Registry
- **데이터베이스**: MongoDB, MariaDB/MySQL, PostgreSQL
- **캐시**: Redis
- **컨테이너**: Docker (Docker Compose 사용)
- **배포**: Kubernetes, Argo CD (Kustomize, Image Updater), Jenkins
- **이미지 빌드**: Jib (`eclipse-temurin:21-jre` 베이스)
- **화면**: React 19, TypeScript, Vite, Tailwind CSS, TanStack Query (nginx 컨테이너로 배포)
- **테스트**: JUnit 5, Testcontainers(Postgres), Vitest
- **기타**: Avro 데이터 직렬화

## 프로젝트 구조
프로젝트는 여러 모듈로 나뉘어 있으며, 각 모듈은 `src/main/java`와 `src/test/java`로 소스와 테스트 코드를 분리합니다. 서비스 모듈은 헥사고날 아키텍처를 따라 도메인(`domain-core`, `application-service`)과 어댑터(`dataaccess`, `messaging`, `cache` 등)를 분리합니다. 주요 모듈은 다음과 같습니다:

### 1. `common`
- **`common-application`**: 애플리케이션 핸들러 및 공통 로직.
- **`common-dataaccess`**: 데이터 액세스 레이어(엔티티, 리포지토리 등).
- **`common-domain`**: 도메인 모델(엔티티, 값 객체, 예외).

### 2. `data-migration-service`
- **`data-migration-container`**: 메인 설정(어노테이션, AOP, 데이터소스 라우팅).
- **`data-migration-dataaccess`**: 챕터, 코스, 마이그레이션 정보 관리.
- **`data-migration-cache`**: Redis 기반 마이그레이션 정보·커리큘럼 캐시.
- **`data-migration-domain`**
  - **`data-migration-domain-core`**: 도메인 모델 및 핵심 로직.
  - **`data-migration-application-service`**: 유스케이스 및 포트 정의.
- **`data-migration-messaging`**: Kafka 요청 리스너와 결과 응답 퍼블리셔.

### 3. `transfer-service`
- **`transfer-application`**: REST API. 전송 접수와 조회 API(아래 표)를 제공합니다.
- **`transfer-container`**: 메인 설정 및 서비스.
- **`transfer-dataaccess`**: 전송 및 자재 데이터 관리.
- **`transfer-cache`**: Redis 기반 전송 로그·데이터소스 캐시.
- **`transfer-domain`**
  - **`transfer-domain-core`**: 전송 관련 도메인 모델 및 핵심 로직.
  - **`transfer-application-service`**: 유스케이스, 포트, Outbox 처리.
- **`transfer-messaging`**: 아웃박스 메시지 퍼블리셔와 결과 응답 리스너.

#### 전송 API
| 메서드 | 경로 | 설명 |
|---|---|---|
| `POST` | `/api/v1/transfers` | 전송 접수. 아웃박스에 저장까지 하고 `202 Accepted` + `Location` 을 돌려줍니다 |
| `GET` | `/api/v1/transfers?status=&page=&size=` | 전송 목록(최신순, 청크 진행률 포함) |
| `GET` | `/api/v1/transfers/stats` | 상태별 전송 수 |
| `GET` | `/api/v1/transfers/{id}` | 전송 상세. 사가 단계(`steps`)와 청크 결과를 담습니다 |
| `GET` | `/api/v1/source-configs` | 원본 저장소 설정 목록(접속 계정 제외) |

### 4. `transfer-console`
콘텐츠 운영자가 저작 DB의 커리큘럼을 서비스 시스템에 배포하는 화면입니다. Maven 빌드와 분리된 npm 프로젝트입니다. 기획은 [`docs/console-plan.md`](docs/console-plan.md)에 있습니다.
- **대시보드**: 오늘 배포 건수, 진행 중, 확인이 필요한 실패, 원본·대상 연결 상태
- **원본 저장소**: 추가·수정·삭제, 연결 테스트, 사용 켜기/끄기
- **대상 시스템**: 연결 확인, 배포 허용 켜기/끄기, 대상별 최근 배포와 반영된 커리큘럼
- **콘텐츠**: 원본의 커리큘럼 검색, 트리 미리보기, 예상 청크 수, 대상별 배포 현황
- **새 배포**: 콘텐츠 → 대상 → 확인. 운영 환경은 확인 체크, 진행 중인 같은 배포는 막음
- **배포 이력·상세**: 상태·대상·기간·요청자 조건, 청크별 내용과 실패 사유, 실패 청크 재전송
- **대상 데이터 확인**: 대상에 실제로 들어간 트리를 원본과 개수·과목 단위로 비교
- **모의 데이터 모드**: 백엔드 없이 브라우저 안에서 위 흐름이 모두 돕니다. 만든 데이터는 브라우저에 남고 사이드바에서 초기화합니다. `CUR-FAIL-01` 을 배포하면 청크 두 개가 실패하고, 호스트에 `down` 이 들어간 원본은 연결에 실패합니다.

> 실제 모드로 쓰려면 화면이 정한 API를 백엔드에 만들어야 합니다(기획 문서 4장). 지금 백엔드에는 배포 목록·상세·집계·접수와 원본 목록만 있습니다.

### 5. `config-service`
- Spring Cloud Config 서버. 각 서비스의 설정 파일을 중앙에서 제공합니다.

### 6. `infrastructure`
- **`kafka/kafka-support`**: Kafka 에러 처리(재시도·DLT 등) 스프링 부트 자동 설정.
- **`message-contracts`**: 서비스 간 메시지 계약(Avro 스키마). 각 messaging 모듈이 아티팩트에서 스키마를 꺼내 자기 클래스를 만듭니다. ([ADR 0001](docs/adr/0001-message-contract-distribution.md))
- **`outbox`**: Transactional Outbox 상태 및 스케줄러.
- **`saga`**: Saga 상태·단계 정의.
- **`docker-compose`**: 로컬 개발용 Kafka, Zookeeper, MongoDB, MySQL, PostgreSQL, Redis 설정.
- **`k8s`**: Kubernetes 매니페스트(Kafka, MongoDB, MySQL, PostgreSQL, 서비스, Argo CD).
- **`jenkins`**: Jenkins Helm values 및 Maven 캐시 PVC.

## 설치 및 실행 방법
1. **필수 소프트웨어 설치**
   - Java 21 이상
   - Maven (또는 동봉된 `mvnw` 사용)
   - Docker 및 Docker Compose

2. **프로젝트 클론**
   ```bash
   git clone https://github.com/brunosong/transfersystem.git
   cd transfersystem
   ```

3. **빌드**
   ```bash
   ./mvnw clean install
   ```
   messaging 모듈은 `message-contracts` 아티팩트에서 스키마를 꺼내므로, 처음에는 `install`을 한 번 돌린 뒤에 `test` 등 다른 목표를 실행합니다.

   컨테이너 이미지는 빌드 단계에 묶여 있지 않으므로 필요할 때만 Jib으로 만듭니다.
   ```bash
   # (먼저 ./mvnw install 로 모듈들이 로컬 저장소에 있어야 한다)
   # 로컬 도커 데몬에 이미지 생성
   ./mvnw -pl transfer-service/transfer-container jib:dockerBuild
   # 레지스트리로 푸시
   ./mvnw -pl transfer-service/transfer-container jib:build
   ```

4. **인프라 실행**
   ```bash
   cd infrastructure/docker-compose
   # 주키퍼
   docker-compose -f common.yml -f zookeeper.yml up -d
   # 카프카 클러스터
   docker-compose -f common.yml -f kafka_cluster.yml up -d
   # 카프카 토픽 초기화
   docker-compose -f common.yml -f init-kafka.yml up -d
   # 데이터베이스 및 캐시 (필요한 것만 실행)
   docker-compose -f mongodb.yml up -d
   docker-compose -f mysql.yml up -d
   docker-compose -f postgres.yml up -d
   docker-compose -f redis.yml up -d
   ```

5. **화면(transfer-console) 실행**
   ```bash
   cd transfer-console
   npm install
   npm run dev:mock   # 백엔드 없이 모의 데이터로 실행 (http://localhost:5173)
   npm run dev        # 실제 서비스에 붙어서 실행
   ```
   개발 서버가 `/api/v1/targets` 는 data-migration-service(기본 http://localhost:8001)로, 나머지 `/api` 와 `/healthcheck` 는 transfer-service(기본 http://localhost:8080)로 넘기므로 CORS 설정은 필요 없습니다. 다른 주소를 쓰려면 `DATA_MIGRATION_SERVICE_URL`, `TRANSFER_SERVICE_URL` 환경 변수를 지정합니다.

   컨테이너 이미지는 nginx 가 정적 파일을 내보내고 같은 규칙으로 `DATA_MIGRATION_SERVICE_URL`, `TRANSFER_SERVICE_URL` 로 프록시합니다.
   ```bash
   docker build -t transfer-console transfer-console                         # 실제 API용
   docker build -t transfer-console:demo --build-arg API_MODE=mock transfer-console  # 시연용
   ```

6. **Kubernetes 설정**
   - Kubernetes 클러스터에 배포하려면 `infrastructure/k8s` 디렉토리의 설정 파일을 사용합니다.
   - 인프라 리소스는 `kubectl apply -f infrastructure/k8s/<리소스>/` 명령어로 배포합니다.
   - 서비스는 Argo CD가 `infrastructure/k8s/services`의 Kustomize 설정을 기준으로 배포합니다.
