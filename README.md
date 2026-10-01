# Transfer System 프로젝트

## 개요
`Transfer System`은 데이터 전송, 마이그레이션, 업로드와 관련된 기능을 제공하는 모듈화된 애플리케이션입니다. 이 프로젝트는 다양한 서비스(`data-migration-service`, `transfer-service`, `config-service`)와 공통 모듈(`common`), 공용 인프라 모듈(`infrastructure`)로 구성되어 있으며, Kafka, MongoDB, Redis 등의 인프라를 활용하여 확장성과 유지보수성을 높였습니다.

서비스 간 통신은 **Transactional Outbox** 패턴과 **Saga** 패턴을 기반으로 하며, 메시지 계약(Avro 스키마)은 별도 모듈로 분리해 서비스들이 아티팩트로 공유합니다.

## 프로젝트 목적
- **데이터 전송**: 데이터를 효율적으로 처리하고 전송하는 시스템 구축.
- **데이터 마이그레이션**: 기존 데이터를 새로운 시스템으로 옮기는 서비스 제공.
- **데이터 업로드**: 데이터 업로드 서비스 제공. (현재 개발 X)
- **모듈화**: 공통 로직을 재사용 가능한 모듈로 분리하여 개발 효율성 증대.
- **신뢰성 있는 메시징**: Outbox·Saga 패턴으로 DB 변경과 메시지 발행의 일관성 보장.

## 기술 스택
- **언어**: Java 21
- **빌드 도구**: Maven (Maven Wrapper 포함)
- **프레임워크**: Spring Boot 3.3, Spring Cloud Config
- **메시징**: Kafka, Schema Registry
- **데이터베이스**: MongoDB, MariaDB/MySQL, PostgreSQL
- **캐시**: Redis
- **컨테이너**: Docker (Docker Compose 사용)
- **배포**: Kubernetes, Argo CD (Kustomize, Image Updater), Jenkins
- **기타**: Feign 클라이언트, Avro 데이터 직렬화

## 프로젝트 구조
프로젝트는 여러 모듈로 나뉘어 있으며, 각 모듈은 `src/main/java`와 `src/test/java`로 소스와 테스트 코드를 분리합니다. 서비스 모듈은 헥사고날 아키텍처를 따라 도메인(`domain-core`, `application-service`)과 어댑터(`dataaccess`, `messaging`, `cache` 등)를 분리합니다. 주요 모듈은 다음과 같습니다:

### 1. `common`
- **`common-application`**: 애플리케이션 핸들러 및 공통 로직.
- **`common-dataaccess`**: 데이터 액세스 레이어(엔티티, 리포지토리 등).
- **`common-domain`**: 도메인 모델(엔티티, 값 객체, 예외).

### 2. `data-migration-service`
- **`data-migration-application`**: REST API 및 Kafka 설정.
- **`data-migration-container`**: 메인 설정(어노테이션, AOP, 데이터소스 라우팅).
- **`data-migration-dataaccess`**: 챕터, 코스, 마이그레이션 정보 관리.
- **`data-migration-cache`**: Redis 기반 마이그레이션 정보·커리큘럼 캐시.
- **`data-migration-domain`**
  - **`data-migration-domain-core`**: 도메인 모델 및 핵심 로직.
  - **`data-migration-application-service`**: 유스케이스 및 포트 정의.
- **`data-migration-messaging`**: Kafka 기반 메시징 처리.

### 3. `dataupload-service`
> 현재 빌드에서 제외되어 있으며, `dataupload-service추가` 브랜치에서 관리합니다.

- **`dataupload-container`**: 업로드 도메인 설정.
- **`dataupload-dataaccess`**: 업로드 데이터 액세스 레이어.
- **`dataupload-domain`**: 업로드 관련 도메인 로직.

### 4. `transfer-service`
- **`transfer-application`**: REST API 및 예외 처리.
- **`transfer-container`**: 메인 설정 및 서비스.
- **`transfer-dataaccess`**: 전송 및 자재 데이터 관리.
- **`transfer-cache`**: Redis 기반 전송 로그·데이터소스 캐시.
- **`transfer-domain`**
  - **`transfer-domain-core`**: 전송 관련 도메인 모델 및 핵심 로직.
  - **`transfer-application-service`**: 유스케이스, 포트, Outbox 처리.
- **`transfer-infrastructure`**: 외부 API 호출(Feign) 및 어댑터.
- **`transfer-messaging`**: Kafka 기반 퍼블리셔.

### 5. `config-service`
- Spring Cloud Config 서버. 각 서비스의 설정 파일을 중앙에서 제공합니다.

### 6. `infrastructure`
- **`kafka/kafka-support`**: Kafka 에러 처리(재시도·DLT 등) 스프링 부트 자동 설정.
- **`message-contracts`**: 서비스 간 메시지 계약(Avro 스키마). 각 서비스가 아티팩트로 의존합니다.
- **`outbox`**: Transactional Outbox 상태 및 스케줄러.
- **`saga`**: Saga 상태·단계 정의.
- **`docker-compose`**: 로컬 개발용 Kafka, Zookeeper, MongoDB, MySQL, PostgreSQL, Redis 설정.
- **`k8s`**: Kubernetes 매니페스트(Kafka, MongoDB, MySQL, PostgreSQL, 서비스, Argo CD).
- **`jenkins`**: Jenkins Helm values 및 Maven 캐시 PVC.

### 7. `study-api`
- 학습·실험용 모듈.

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

3. **의존성 설치**
   ```bash
   ./mvnw clean install
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

5. **Kubernetes 설정**
   - Kubernetes 클러스터에 배포하려면 `infrastructure/k8s` 디렉토리의 설정 파일을 사용합니다.
   - 인프라 리소스는 `kubectl apply -f infrastructure/k8s/<리소스>/` 명령어로 배포합니다.
   - 서비스는 Argo CD가 `infrastructure/k8s/services`의 Kustomize 설정을 기준으로 배포합니다.
