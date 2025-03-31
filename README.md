# Transfer System 프로젝트

## 개요
`Transfer System`은 데이터 전송, 마이그레이션, 업로드와 관련된 기능을 제공하는 모듈화된 애플리케이션입니다. 이 프로젝트는 다양한 서비스(`data-migration-service`, `dataupload-service`, `transfer-service`)와 공통 모듈(`common`)로 구성되어 있으며, Kafka, MongoDB 등의 인프라를 활용하여 확장성과 유지보수성을 높였습니다.

## 프로젝트 목적
- **데이터 전송**: 데이터를 효율적으로 처리하고 전송하는 시스템 구축.
- **데이터 마이그레이션**: 기존 데이터를 새로운 시스템으로 옮기는 서비스 제공.
- **데이터 업로드**: 데이터 업로드 서비스 제공. (현재 개발 X)
- **모듈화**: 공통 로직을 재사용 가능한 모듈로 분리하여 개발 효율성 증대.

## 기술 스택
- **언어**: Java
- **빌드 도구**: Maven
- **프레임워크**: Spring Boot
- **메시징**: Kafka
- **데이터베이스**: MongoDB, Mariadb, PostgreSQL
- **컨테이너**: Docker (Docker Compose 사용)
- **기타**: Feign 클라이언트, Avro 데이터 직렬화

## 프로젝트 구조
프로젝트는 여러 모듈로 나뉘어 있으며, 각 모듈은 `src/main/java`와 `src/test/java`로 소스와 테스트 코드를 분리합니다. 주요 모듈은 다음과 같습니다:

### 1. `common`
- **`common-application`**: 애플리케이션 핸들러 및 공통 로직.
- **`common-dataaccess`**: 데이터 액세스 레이어(엔티티, 리포지토리 등).
- **`common-domain`**: 도메인 모델(엔티티, 값 객체, 예외).

### 2. `data-migration-service`
- **`data-migration-application`**: REST API 및 Kafka 설정.
- **`data-migration-container`**: 도메인 설정(어노테이션, AOP, 데이터소스).
- **`data-migration-dataaccess`**: 챕터, 코스, 마이그레이션 정보 관리.
- **`data-migration-domain`**: 도메인 로직 및 서비스.
- **`data-migration-messaging`**: Kafka 기반 메시징 처리.

### 3. `dataupload-service`
- **`dataupload-container`**: 업로드 도메인 설정.
- **`dataupload-dataaccess`**: 업로드 데이터 액세스 레이어.
- **`dataupload-domain`**: 업로드 관련 도메인 로직.

### 4. `transfer-service`
- **`transfer-application`**: REST API 및 예외 처리.
- **`transfer-container`**: 메인 설정 및 서비스.
- **`transfer-dataaccess`**: 전송 및 자재 데이터 관리.
- **`transfer-domain`**: 전송 관련 도메인 로직.
- **`transfer-infrastructure`**: 외부 API 호출(Feign) 및 어댑터.
- **`transfer-messaging`**: Kafka 기반 퍼블리셔.

### 5. `infrastructure`
- **`docker-compose`**: Kafka, MongoDB, Zookeeper 설정.
- **`kafka`**: Kafka 설정, 모델, 컨슈머, 프로듀서 구현.



## 설치 및 실행 방법
1. **필수 소프트웨어 설치**
  - Java 21 이상
  - Maven
  - Docker 및 Docker Compose

2. **프로젝트 클론**
   ```bash
   git clone https://bitbucket.org/songbrunosong/transfersystem.git
   cd transfer-system
   
3. **의존성 설치**
   ```bash
   mvn clean install

4. **인프라 실행**
   ```bash
   cd infrastructure/docker-compose
   # 주키퍼
   docker-compose -f common.yml -f zookeeper.yml up -d
   # 카푸카 클러스터
   docker-compose -f common.yml -f kafka_cluster.yml up -d
   # 카푸카 init
   docker-compose -f init-kafka up -d
   
