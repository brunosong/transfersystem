# 원본을 읽어 청크로 나누는 흐름

> 배포를 접수할 때 **원본 저장소에서 커리큘럼 문서 하나를 꺼내 청크 목록으로 바꾸기까지**를 다룬다.
> 청크를 저장하고 아웃박스에 넣는 뒤쪽 일은 다루지 않는다([시스템 설계 2장](architecture.md#2-처리-흐름과-트랜잭션)).
> 2026-11-11 코드 기준이다. 클래스 이름은 코드 그대로 적었다.

## 0. 한눈에

네 부분으로 나뉜다. 각 부분은 아래 장에서 따로 본다.

```mermaid
flowchart TD
  REQ["POST /api/v1/deployments<br/>DeploymentRequest"] --> P1

  subgraph P1["1장. 요청 받기"]
    C[DeploymentController] --> S[DeploymentApplicationServiceImpl]
  end

  P1 --> H[SourceFindDataHandler.findSourceContentData]

  H --> P2
  subgraph P2["2장. 원본 설정 찾기"]
    CH[SourceConfigHelper] --> CR[SourceConfigRepositoryImpl] --> PGDB[(Postgres<br/>sourceconfig 표)]
  end

  H --> P3
  subgraph P3["3장. 원본 문서 꺼내기"]
    FH[SourceFindHelper] --> SR[SourceRepositoryImpl] --> DC[DynamicDataSourceConnector]
    DC --> MG[MongoConnectHelper] --> MDB[(저작 DB<br/>Mongo)]
    DC --> MY[MysqlConnectHelper] --> YDB[(MySQL)]
  end

  H --> P4
  subgraph P4["4장. 청크로 나누기"]
    DH[DeploymentDataConvertHelper] --> CV[CurriculumSourceConverter]
  end

  P4 --> OUT["SourceContentData<br/>curriculumId + chunkDataList"]
  OUT --> NEXT["DeploymentExecutionHandler.sendData<br/>(저장과 아웃박스, 이 문서 밖)"]
```

`SourceFindDataHandler`가 2, 3, 4장을 차례로 부르는 조율자다.

```java
SourceConfig sourceConfig = sourceConfigHelper.findSourceConfigInfo(sourceConfigId);          // 2장
SourceContentData data = sourceFindHelper.findSourceContentData(sourceId, sourceConfig);      // 3장
List<ChunkContent> chunks = deploymentDataConvertHelper.splitCurriculumJsonIntoChunks(data);  // 4장
data.updateChunkDataList(chunks);
```

### 객체가 바뀌는 순서

같은 데이터가 단계마다 어떤 모양이 되는지 먼저 본다.

```mermaid
flowchart LR
  A["DeploymentRequest<br/>sourceConfigId: Long<br/>sourceId: String"] --> B["Deployment<br/>SourceConfigId, SourceId"]
  B --> C["SourceConfig<br/>접속 정보"]
  C --> D["Mongo Document"]
  D --> E["byte[]<br/>JSON"]
  E --> F["SourceContentData<br/>jsonData 채움"]
  F --> G["JsonNode<br/>트리"]
  G --> H["List&lt;ChunkContent&gt;<br/>뼈대 1 + 과목 N"]
  H --> I["SourceContentData<br/>curriculumId, chunkDataList 채움"]
```

| 단계 | 객체 | 있는 모듈 | 누가 만드나 |
|---|---|---|---|
| 요청 | `DeploymentRequest` | application-service (`dto/create`) | Spring이 JSON 본문으로 만든다 |
| 배포 | `Deployment` | domain-core | `DeploymentDataMapper` |
| 원본 설정 | `SourceConfigEntity` → `SourceConfig` | dataaccess → domain-core | JPA, `SourceDataAccessMapper` |
| 원본 문서 | `Document` → `byte[]` | dataaccess | Mongo 드라이버, `MongoConnectHelper.toJson` |
| 원본 데이터 | `SourceContentData` | domain-core | `SourceDataAccessMapper` |
| 파싱한 트리 | `JsonNode` | application-service 안에서만 | `ObjectMapper.readTree` |
| 청크 내용 | `ChunkContent` | domain-core | `CurriculumSourceConverter` |

## 1. 요청 받기

```mermaid
sequenceDiagram
  autonumber
  participant OP as 운영자(화면)
  participant C as DeploymentController
  participant S as DeploymentApplicationServiceImpl
  participant M as DeploymentDataMapper
  participant H as SourceFindDataHandler

  OP->>C: POST /api/v1/deployments<br/>{targetId, transType, sourceId, sourceConfigId}
  C->>C: @Valid 검사 (네 칸 모두 @NotNull)
  C->>S: executeDeployment(request)
  S->>M: deploymentRequestToDeployment(request)
  M-->>S: Deployment (SourceConfigId, SourceId 값 객체)
  S->>H: findSourceContentData(sourceConfigId, sourceId)
  H-->>S: SourceContentData
  S->>S: sendData(deployment, data) — 이 문서 밖
  S-->>C: CreateDeploymentResponse(deploymentId, 청크 수)
  C-->>OP: 202 Accepted + Location
```

요청 본문에서 이 문서에 관계있는 칸은 둘이다.

| 칸 | 뜻 | 예 |
|---|---|---|
| `sourceConfigId` | **어느 원본 저장소**에서 읽을지. `sourceconfig` 표의 키 | `1` |
| `sourceId` | 그 저장소 안의 **어느 문서**인지. Mongo면 `_id`(24자리 16진수) | `"6731a2b4c5d6e7f801234567"` |

이름이 비슷해서 헷갈린다. `sourceConfigId`는 "어디서", `sourceId`는 "무엇을"이다([용어집](../domain/glossary.md)의 원본 저장소).

## 2. 원본 설정 찾기

```mermaid
flowchart TD
  A["SourceConfigHelper.findSourceConfigInfo(SourceConfigId)"] --> B["SourceConfigRepository (포트)<br/>findSourceConfigInfo(Long)"]
  B --> C["SourceConfigRepositoryImpl (어댑터)"]
  C --> D["SourceConfigJpaRepository.findById"]
  D --> E[(Postgres sourceconfig)]
  E --> F{행이 있나}
  F -- 없음 --> X["MaterialNotFoundException<br/>→ 400"]
  F -- 있음 --> G["SourceDataAccessMapper.entityToSourceConfig<br/>SourceConfigEntity → SourceConfig"]
  G --> H[SourceConfig 반환]
```

`SourceConfig`는 접속 정보 묶음이다. 표의 칸이 그대로 옮겨진다.

```mermaid
classDiagram
  class SourceConfig {
    SourceType sourceType
    DbType dbType
    String host
    int port
    String database
    String username
    String password
    String tableOrCollection
  }
```

| 칸 | 값 | 쓰는 곳 |
|---|---|---|
| `sourceType` | `CURRICULUM`, `EXAM_RESULT_DATA` | 4장. 커리큘럼으로 나눌지 정한다 |
| `dbType` | `MONGO`, `MYSQL`, `POSTGRESQL`, `ORACLE`, `H2` | 3장. 어느 헬퍼로 읽을지 정한다 |
| `host`, `port`, `database`, `username`, `password` | 접속 정보 | 3장. 연결 풀을 만든다 |
| `tableOrCollection` | Mongo면 컬렉션, MySQL이면 표 이름 | 3장. 조회 대상 |

## 3. 원본 문서 꺼내기

### 3.1 호출 순서

```mermaid
sequenceDiagram
  autonumber
  participant FH as SourceFindHelper
  participant SR as SourceRepositoryImpl
  participant DC as DynamicDataSourceConnector
  participant MG as MongoConnectHelper
  participant DB as 저작 DB (Mongo)
  participant MP as SourceDataAccessMapper

  FH->>SR: findData(sourceId, sourceConfig)
  SR->>SR: sourceId가 null이면 IllegalArgumentException
  SR->>DC: query(sourceConfig, sourceId)
  DC->>MG: queryMongo(sourceConfig, sourceId)  [dbType = MONGO]
  MG->>MG: clientFor(sourceConfig) — 3.3
  MG->>DB: findOne({_id: ObjectId(sourceId)}) in tableOrCollection
  DB-->>MG: Document 또는 null
  MG->>MG: toJson(Document) — 3.4
  MG-->>DC: JSON 바이트 (없으면 빈 Optional)
  DC-->>SR: JSON 바이트
  SR->>MP: mapToSourceContentData(jsonData, sourceId, sourceConfig)
  MP-->>SR: SourceContentData
  SR-->>FH: SourceContentData (없으면 빈 Optional)
  FH->>FH: 비었으면 MaterialNotFoundException (→ 400)
```

### 3.2 DB 종류로 길이 갈린다

```mermaid
flowchart TD
  A["DynamicDataSourceConnector.query"] --> B{sourceConfig.dbType}
  B -- MONGO --> M["MongoConnectHelper.queryMongo<br/>드라이버가 바로 JSON을 쓴다"]
  B -- MYSQL --> Y["MysqlConnectHelper.queryMySql<br/>SELECT * FROM 표 WHERE id = ?<br/>→ Map(칸 이름 → 값)"]
  Y --> J["ObjectMapper.writeValueAsBytes(Map)<br/>Spring의 ObjectMapper라 날짜는 ISO 문자열"]
  B -- "POSTGRESQL, ORACLE, H2" --> X["UnsupportedOperationException<br/>→ 500"]
  M --> R["Optional&lt;byte[]&gt;"]
  J --> R
```

어느 길로 와도 결과는 **JSON 바이트**다. 원본마다 JSON을 만드는 방법이 달라서 여기서 통일한다.

경계를 `JsonNode`나 `Document`가 아니라 `byte[]`로 둔 이유: `SourceContentData`는 domain-core에 있고, domain-core는 Jackson도 BSON도 모른다.

### 3.3 연결 풀은 원본 설정마다 하나

```mermaid
flowchart TD
  A["clientFor(sourceConfig)"] --> K["키 만들기<br/>mongo:host:port:database:username<br/>(비밀번호는 넣지 않는다)"]
  K --> C{"clients 맵에<br/>이 키가 있나"}
  C -- 있음 --> R[있는 MongoClient 반환]
  C -- 없음 --> N["createClient(sourceConfig)<br/>MongoCredential + ServerAddress"]
  N --> P[맵에 넣고 반환]

  E["DataSourceCacheEvent<br/>(DATA_SOURCE_CACHE_DELETE)"] --> CL["closeAll()<br/>모두 close 후 비움"]
  SD["앱 종료 (@PreDestroy)"] --> CL
```

- "있나 보고, 없으면 만들어 넣기"를 `ConcurrentHashMap.computeIfAbsent` 한 번으로 한다. 같은 키로 동시에 와도 풀은 하나만 생긴다.
- MySQL(`MysqlConnectHelper`)도 같은 모양이다. 키는 `datasource:dbType:host:port:database`이고, 값은 `HikariDataSource`다.
- 캐시를 비우는 이벤트는 받는 쪽만 있다. **보내는 곳은 아직 없다.** 원본 설정을 고치는 기능이 생기면 그곳에서 보낸다.

### 3.4 Mongo 문서를 JSON으로 바꾸는 규칙

`MongoConnectHelper.JSON_SETTINGS`가 정한다. 대상이 Mongo 전용 모양을 몰라도 되게 하려는 것이다([ADR 0006](../adr/0006-chunk-content-contract.md)).

| Mongo 값 | JSON | 예 |
|---|---|---|
| `ObjectId` | 16진 문자열 | `"6731a2b4c5d6e7f801234567"` |
| `Date` | ISO-8601 UTC 문자열 | `"2026-11-11T00:00:00Z"` |
| `Int32`, `Int64`, `Double` | 숫자 그대로 (RELAXED 모드) | `3` |
| 문자열, 배열, 문서 | 그대로 | |

### 3.5 이 단계가 끝나면

```mermaid
classDiagram
  class SourceContentData {
    String sourceId
    SourceType sourceType
    DbType dbType
    byte[] jsonData
    String curriculumId
    List~ChunkContent~ chunkDataList
    int chunkOffset
  }
```

| 칸 | 3장이 끝난 뒤 | 채우는 곳 |
|---|---|---|
| `sourceId` | 채움 | 요청의 `sourceId` |
| `sourceType`, `dbType` | 채움 | 원본 설정 |
| `jsonData` | 채움 | 원본 문서 JSON |
| `curriculumId` | 비어 있음 | 4장 `toCurriculumId` |
| `chunkDataList` | 비어 있음 | 4장 결과를 `SourceFindDataHandler`가 넣는다 |
| `chunkOffset` | 쓰지 않음 | 결과 처리 쪽의 옛 코드가 쓴다 |

## 4. 청크로 나누기

### 4.1 원본 문서의 모양

저작 DB의 문서 하나가 커리큘럼 하나다. 칸 이름은 `CurriculumBaseKey`에 있다.

```mermaid
flowchart TD
  ROOT["문서 { _id, curriculum }"] --> CUR["curriculum<br/>curriculumId, title, description"]
  CUR --> G["grade[] (LEVEL1)<br/>id, title, description"]
  G --> S["semester[] (LEVEL2)<br/>id, title, description"]
  S --> SUB["subject[] (LEVEL3)<br/>id, title, description"]
  SUB --> U["unit[] (LEVEL4)<br/>id, title, description, …"]
  U --> L["lesson[] (LEVEL5)<br/>id, title, description, lessonNumber, learningLevel, …"]
```

예(줄였다):

```json
{
  "_id": "6731a2b4c5d6e7f801234567",
  "curriculum": {
    "curriculumId": "CUR001", "title": "중등", "description": "중등 과정",
    "grade": [{
      "id": "G1", "title": "1학년",
      "semester": [{
        "id": "S1", "title": "1학기",
        "subject": [
          { "id": "SUB1", "title": "국어",
            "unit": [{ "id": "U1", "title": "단원 1",
              "lesson": [{ "id": "L1", "title": "차시 1", "lessonNumber": 1, "learningLevel": 3, "format": "Video" }] }] },
          { "id": "SUB2", "title": "수학", "unit": [] }
        ]
      }]
    }]
  }
}
```

### 4.2 나누는 순서

```mermaid
flowchart TD
  A["DeploymentDataConvertHelper.splitCurriculumJsonIntoChunks"] --> B["objectMapper.readTree(jsonData)<br/>→ rootNode"]
  B --> C["rootNode.path(curriculum)<br/>→ curriculumNode"]
  C --> D{"sourceType == CURRICULUM?"}
  D -- 아니오 --> E["빈 목록 반환<br/>(EXAM_RESULT_DATA는 아직 없음)"]
  D -- 예 --> F["toCurriculumId<br/>→ SourceContentData.curriculumId"]
  F --> G["toSkeleton → 목록 0번"]
  G --> H["toSubjectChunks → 목록 1번부터"]
  H --> I["List&lt;ChunkContent&gt; 반환"]
```

`SourceFindDataHandler`가 이 목록을 `SourceContentData.chunkDataList`에 넣는다. 목록의 순서가 곧 청크 번호다.

### 4.3 문서 하나가 청크 여러 개가 된다

위 예의 문서는 청크 셋이 된다.

```mermaid
flowchart LR
  DOC["문서 CUR001<br/>학년 1, 학기 1, 과목 2"] --> K0
  DOC --> K1
  DOC --> K2

  K0["0번: Skeleton (뼈대)<br/>커리큘럼 제목<br/>G1 → S1 → 과목 ID [SUB1, SUB2]<br/>단원·차시는 없다"]
  K1["1번: SubjectChunk<br/>gradeId G1, semesterId S1<br/>SUB1 국어 → U1 → L1"]
  K2["2번: SubjectChunk<br/>gradeId G1, semesterId S1<br/>SUB2 수학 (단원 없음)"]
```

- **뼈대(0번)**는 학기까지와 학기별 과목 ID 목록만 담는다. 대상은 이 목록에 없는 과목을 서비스에서 뺀다([이벤트와 메시지 6절](../domain/events.md#6-원본에서-지운-과목은-대상에서도-뺀다)).
- **과목 청크(1번부터)**는 과목 하나와 그 아래 단원, 차시 전부를 담는다. 어느 학년, 학기의 과목인지도 함께 담는다.
- 과목 청크 수는 문서 전체의 과목 수다. 시드 문서(학년 4 × 학기 2 × 과목 5)라면 뼈대 1 + 과목 40 = 청크 41개다.

### 4.4 만들어지는 객체

`ChunkContent`(domain-core)는 sealed interface이고, 청크는 `Skeleton`이나 `SubjectChunk` 둘 중 하나다.

```mermaid
classDiagram
  class ChunkContent {
    <<sealed interface>>
  }
  class Skeleton {
    String title
    String description
    List~Grade~ grades
  }
  class SubjectChunk {
    String gradeId
    String semesterId
    Subject subject
  }
  class Grade {
    String id
    String title
    String description
    List~Semester~ semesters
  }
  class Semester {
    String id
    String title
    String description
    List~String~ subjectIds
  }
  class Subject {
    String id
    String title
    String description
    List~Unit~ units
  }
  class Unit {
    String id
    String title
    String description
    String attributes
    List~Lesson~ lessons
  }
  class Lesson {
    String id
    String title
    String description
    int lessonNumber
    int learningLevel
    String attributes
  }

  ChunkContent <|.. Skeleton
  ChunkContent <|.. SubjectChunk
  Skeleton --> Grade
  Grade --> Semester
  SubjectChunk --> Subject
  Subject --> Unit
  Unit --> Lesson
```

### 4.5 어느 칸이 어디로 가나

| 원본 칸 | 필수? | 어디로 |
|---|---|---|
| `curriculum.curriculumId` | 필수 | `SourceContentData.curriculumId` (청크 안에는 없다) |
| 모든 단계의 `id`, `title` | 필수 | 같은 이름의 칸 |
| 모든 단계의 `description` | 없어도 됨 | 같은 이름의 칸, 없으면 `null` |
| 차시 `lessonNumber` | 필수 (정수) | `Lesson.lessonNumber` |
| 차시 `learningLevel` | 필수 (정수) | `Lesson.learningLevel`과 `Lesson.attributes` 둘 다. 받는 쪽이 옮겨 가면 `attributes`에만 남는다 |
| 단원, 차시의 **나머지 칸 전부** | 검사 안 함 | `attributes` (JSON 객체 문자열, 원본 순서 그대로) |
| 학년, 학기, 과목의 나머지 칸 | | 버린다 |
| 문서의 `_id` | | 버린다 (`sourceId`로 이미 안다) |

위 예의 차시 `L1`은 이렇게 된다.

```text
Lesson(id="L1", title="차시 1", description=null, lessonNumber=1, learningLevel=3,
       attributes="{\"learningLevel\":3,\"format\":\"Video\"}")
```

단원 `U1`은 아는 칸과 `lesson` 말고는 칸이 없어서 `attributes="{}"`다.

### 4.6 검사

원본 칸 이름을 아는 곳은 `CurriculumSourceConverter` 한 곳뿐이다. 검사도 여기서 한다.

| 메서드 | 하는 일 | 실패하면 |
|---|---|---|
| `requiredText(node, 칸)` | 문자열이 있어야 한다 | `IllegalArgumentException("저작 문서에 문자열 필드 'title' 가 없습니다: id=L1")` |
| `requiredInt(node, 칸)` | 정수가 있어야 한다 | 같은 모양, "정수 필드" |
| `optionalText(node, 칸)` | 문자열이면 값, 아니면 `null` | 실패하지 않는다 |

오류 메시지에는 문서 전체가 아니라 그 노드의 `id`만 넣는다. 문서가 커서다.

## 5. 실패하면

| 어디서 | 상황 | 예외 | 응답 |
|---|---|---|---|
| 1장 | 요청 칸이 비었다 | `MethodArgumentNotValidException` | 400, 어느 칸인지 |
| 2장 | `sourceConfigId`에 맞는 설정이 없다 | `MaterialNotFoundException` | 400 |
| 3장 | `sourceId`가 ObjectId 모양(24자리 16진수)이 아니다 | `IllegalArgumentException` | 500 |
| 3장 | 지원하지 않는 DB 종류다 | `UnsupportedOperationException` | 500 |
| 3장 | Mongo에 붙지 못한다 | `MongoTimeoutException` (서버 선택 30초 뒤) | 500 |
| 3장 | 문서가 없다 | `MaterialNotFoundException` | 400 |
| 4장 | 필수 칸이 없거나 타입이 다르다 | `IllegalArgumentException`을 `RuntimeException`으로 감쌈 | 500 |

500은 공통 처리기(`GlobalExceptionHandler`)가 `"Unexpected error!"`로 돌려준다. 그래서 운영자는 무엇이 잘못됐는지 알 수 없다. 아래 6장에 적었다.

## 6. 지금 남은 문제

| # | 문제 | 고칠 방향 |
|---|---|---|
| 1 | 4장의 필수 칸 오류가 500이 된다. `DeploymentDataConvertHelper`가 모든 예외를 `RuntimeException`으로 감싸기 때문이다 | 감싸지 않거나 원본 문서 오류용 예외를 두고 422로 돌려준다. 어느 차시의 어느 칸인지 운영자가 봐야 한다 |
| 2 | `sourceId`가 ObjectId 모양이 아니면 500이 된다 | 400으로 돌려준다 |
| 3 | 원본 설정이 없을 때 메시지가 "Not found sourceContentData"다 | "원본 저장소가 없다"로 고친다 |
| 4 | MySQL 원본은 행 하나를 평평한 Map으로 읽는다. 변환기는 `curriculum` 아래 트리를 기대하므로 MySQL 원본으로는 커리큘럼을 나눌 수 없다 | MySQL 원본을 계속 둘지 정한다. 둔다면 행을 트리로 바꾸는 방법이 따로 있어야 한다 |
| 5 | `sourceType`이 `EXAM_RESULT_DATA`면 오류 없이 청크 0개가 된다 | 지원하지 않는다고 거절한다 |
| 6 | `SourceContentData`가 원본 JSON(`jsonData`)을 청크로 나눈 뒤에도 들고 다닌다 | [7장](#7-원본-문서-커리큘럼-청크를-나눈다)대로 꺼낸 문서, 커리큘럼, 청크로 나눈다 |
| 7 | 캐시를 비우는 `DataSourceCacheEvent`를 보내는 곳이 없다 | 원본 설정 수정 기능을 만들 때 보낸다 |

## 7. 원본 문서, 커리큘럼, 청크를 나눈다

> 2026-11-12에 정했다. 0~6장은 바꾸기 전 코드를 적은 것이다. 코드를 바꾸면 그 장들도 고친다.

### 7.1 문제

`SourceContentData`는 `valueobject` 패키지에 있지만 값 객체가 아니다.

| 값 객체라면 | 지금 |
|---|---|
| 만든 뒤 바뀌지 않는다 | `updateCurriculumId`, `updateChunkDataList`로 바뀐다 |
| 값이 같으면 같은 것이다 | `equals`가 없다. `byte[]`는 값으로 비교되지도 않는다 |
| 뜻이 하나다 | 꺼낸 문서(JSON), 문서 안의 커리큘럼 ID, 나눈 청크가 한 객체에 섞여 있다 |

그래서 이런 일이 생긴다.

- `splitCurriculumJsonIntoChunks`는 청크 목록을 돌려주면서, 받은 인자의 `curriculumId`도 몰래 채운다. 시그니처만 봐서는 모른다.
- 나눈 뒤에도 원본 JSON을 들고 다니고, `Deployment`가 이 객체를 칸으로 들고 있다. [도메인 모델](../domain/model.md#청크-내용은-애그리거트에-넣지-않는다)의 "청크 내용은 애그리거트에 넣지 않는다"와 어긋난다.
- `chunkOffset` 칸 하나를 여러 뜻으로 쓴다. 접수 때는 아무도 채우지 않는데 `DeploymentDataAccessMapper`가 이것을 `totalChunkSize`로 저장한다. [ROADMAP](../ROADMAP.md)의 "`totalChunkSize`가 0으로 저장됨"이 이것이다.
- 청크로 나누는 일이 원본 쪽 변환기(`CurriculumSourceConverter`) 안에 있다. [컨텍스트 맵](../domain/context-map.md)은 "원본은 커리큘럼 트리만 주고 배포가 나눈다"고 정했다. 어떻게 나눌지(뼈대 먼저, 과목은 따로)는 배포의 규칙이기 때문이다.

### 7.2 선택지

| 안 | 내용 | 얻는 것 | 남는 문제 |
|---|---|---|---|
| A | 값 객체가 아니라고 인정하고 패키지만 옮긴다 | 고칠 곳이 적다 | 위 문제가 그대로다 |
| B | 불변으로 바꾸고 `withChunks(...)`처럼 복사본을 돌려준다 | 숨은 부작용이 없어진다 | 여러 뜻이 한 객체에 섞여 있고, 원본 JSON도 계속 따라다닌다 |
| **C** | **단계마다 객체를 따로 둔다.** 원본은 커리큘럼 트리를 주고, 배포가 그것을 청크로 나눈다 | 단계마다 필요한 칸만 있다. 컨텍스트 맵의 경계와 맞는다 | `Deployment`와 저장·메시징 매퍼까지 고쳐야 한다 |

**C를 고른다.** 흐름이 원래 "꺼낸다 → 커리큘럼으로 바꾼다 → 청크로 나눈다"라서 객체도 그대로 나누는 것이 가장 자연스럽다.

### 7.3 한눈에

```mermaid
flowchart LR
  subgraph SRC["원본 컨텍스트"]
    CFG["SourceConfig<br/>어디서 읽나"] --> DOC["SourceDocument<br/>꺼낸 문서 (JSON)"]
    DOC --> CV["CurriculumSourceConverter<br/>(부패 방지 계층)"]
  end
  CV --> CUR["Curriculum<br/>커리큘럼 트리 (값 객체)"]
  subgraph DEP["배포 컨텍스트"]
    CUR --> SP["ChunkContent.split<br/>뼈대 1 + 과목 N"]
    SP --> ACC["Deployment.accept(과목 수)"]
    SP --> PAY["청크 내용 저장"]
  end
```

| 객체 | 뜻 | 누구 것 | 도메인 객체인가 | 언제까지 쓰나 |
|---|---|---|---|---|
| `SourceConfig` | **어디서** 읽나. 접속 정보 | 원본 | 엔티티 (식별자 있음) | 오래 산다. 운영자가 등록하고 끈다 |
| `SourceDocument` | 꺼낸 문서 그대로 | 원본 | **아니다.** 포트가 돌려주는 데이터다. JSON은 도메인이 해석하지 않는다 | 커리큘럼으로 바꾸면 버린다 |
| `Curriculum` | **무엇을** 배포하나. 학년부터 차시까지의 트리 | 원본이 만들어 배포에 준다 | **값 객체.** 식별자가 없고, 값이 같으면 같은 내용이다 | 청크로 나누면 버린다 |
| `ChunkContent` | **어떻게** 나눠 보내나. 뼈대, 과목 청크 | 배포 | 값 객체 | 저장해 두고 보낼 때 읽는다 |

`Deployment`는 이 중 어느 것도 칸으로 들고 있지 않다. 접수할 때 커리큘럼 ID와 과목 수만 받는다.

### 7.4 SourceDocument

```java
// application-service, 포트 SourceRepository 옆
public record SourceDocument(String sourceId, SourceType sourceType, byte[] json) { }
```

- `SourceRepository.findData`가 이것을 돌려준다. dataaccess 어댑터가 만든다.
- `byte[]`를 가진 `record`라 `equals`가 내용을 비교하지 않는다. 값 객체가 아니고 비교할 일도 없어서 그대로 둔다.
- `dbType`은 넣지 않는다. 어떻게 읽었는지는 꺼낸 뒤에는 알 필요가 없다.

### 7.5 Curriculum

원본 문서의 트리를 그대로 옮긴 것이다. 청크 번호, 뼈대 같은 배포의 말은 들어 있지 않다.

```mermaid
classDiagram
  class Curriculum {
    <<값 객체>>
    String curriculumId
    String title
    String description
    List~Grade~ grades
  }
  class Grade {
    String id
    String title
    String description
    List~Semester~ semesters
  }
  class Semester {
    String id
    String title
    String description
    List~Subject~ subjects
  }
  class Subject {
    String id
    String title
    String description
    List~Unit~ units
  }
  class Unit {
    String id
    String title
    String description
    String attributes
    List~Lesson~ lessons
  }
  class Lesson {
    String id
    String title
    String description
    int lessonNumber
    int learningLevel
    String attributes
  }
  Curriculum --> Grade
  Grade --> Semester
  Semester --> Subject
  Subject --> Unit
  Unit --> Lesson
```

- domain-core `valueobject`에 둔다. 원본과 배포가 함께 쓰는 모델이고, 프레임워크를 모른다.
- 모두 `record`이고 목록은 `List.copyOf`로 복사해 둔다. 만든 뒤 바뀌지 않는다.
- 만들 때 `curriculumId`와 `title`이 비어 있지 않은지 검사한다. 원본 문서의 칸 검사는 지금처럼 변환기가 한다. 변환기는 "문서에 칸이 있나"를, `Curriculum`은 "커리큘럼으로서 말이 되나"를 본다.

**`Subject`, `Unit`, `Lesson`은 `Curriculum` 쪽으로 옮긴다.** 지금은 `ChunkContent` 안에 있다. 과목 청크는 "과목 하나와 그 아래 전부"라서 이 셋을 그대로 쓴다. 그렇다면 청크가 커리큘럼을 가리켜야지, 커리큘럼이 청크의 타입을 빌려 쓰면 안 된다. 청크는 커리큘럼을 나눈 결과이기 때문이다. `Grade`와 `Semester`는 둘 다 두되 모양이 다르다. 커리큘럼의 학기는 과목 목록을 가지고, 뼈대의 학기는 과목 ID 목록만 가진다.

| 타입 | 지금 | 바꾼 뒤 |
|---|---|---|
| `Subject`, `Unit`, `Lesson` | `ChunkContent.Subject` … | `Curriculum.Subject` …. `SubjectChunk`가 `Curriculum.Subject`를 담는다 |
| `Grade`, `Semester` (과목까지 있는 것) | 없다 | `Curriculum.Grade`, `Curriculum.Semester` |
| `Grade`, `Semester` (과목 ID만 있는 것) | `ChunkContent.Grade`, `ChunkContent.Semester` | 그대로 |

메시지 계약(Avro)은 바뀌지 않는다. 메시징 매퍼의 import만 바뀐다.

### 7.6 청크로 나누기

```java
// domain-core, ChunkContent 안
static List<ChunkContent> split(Curriculum curriculum)
```

- 0번은 `Skeleton`이다. 학기까지와 학기별 과목 ID 목록을 담는다.
- 1번부터는 `SubjectChunk`다. 학년과 학기 순서대로, 과목 하나에 하나씩이다.
- 돌려주는 목록은 바꿀 수 없다. 과목 수는 `size() - 1`이다.
- 나누는 규칙이 청크 타입 바로 옆에 있어서, 청크 모양을 바꿀 때 한곳만 보면 된다. 원본 칸 이름은 모른다.

지금 `CurriculumSourceConverter`의 `toSkeleton`, `toSubjectChunks`가 하는 일이다. 변환기는 `toCurriculum` 하나만 남는다.

### 7.7 바뀌는 곳

| 곳 | 지금 | 바꾼 뒤 |
|---|---|---|
| `SourceRepository.findData` | `Optional<SourceContentData>` | `Optional<SourceDocument>` |
| `CurriculumSourceConverter` | `toCurriculumId`, `toSkeleton`, `toSubjectChunks` | `toCurriculum(JsonNode)` 하나 |
| `DeploymentDataConvertHelper` | JSON을 읽고 청크를 돌려주며, 인자를 고친다 | `SourceDocument`를 받아 `Curriculum`을 돌려준다. 인자는 건드리지 않는다 |
| `SourceFindDataHandler` | `SourceContentData`를 돌려준다 | `Curriculum`을 돌려준다 |
| `ChunkContent` | 나누는 일은 밖에 있다 | `split(Curriculum)`을 가진다 |
| `Deployment` | `sourceContentData` 칸, `initializeDeployment(SourceContentData)` | 칸을 지운다 |
| `DeploymentDomainServiceImpl` | `SourceContentData`에서 청크 목록을 꺼낸다 | `Curriculum`을 받아 나눈다 |
| `DeploymentDataAccessMapper` | `sourceType`, `dbType`, `totalChunkSize`를 `SourceContentData`에서 꺼낸다 | `totalChunkSize`는 청크 수로 채운다. `sourceType`, `dbType`은 원본 설정에서 온다 |
| 결과 처리(`DeploymentDataMapper`, `DeploymentRepositoryImpl`) | 결과의 청크 번호를 `SourceContentData.chunkOffset`에 담아 옮긴다 | 청크 번호를 따로 넘긴다 |

`SourceContentData`는 마지막에 지운다.

### 7.8 순서

단계마다 빌드와 테스트가 통과한 상태로 멈춘다.

1. 이 장을 쓴다.
2. `Curriculum`을 만들고 JUnit으로 테스트한다. `Subject`, `Unit`, `Lesson`을 옮긴다.
3. `ChunkContent.split`을 만들고 테스트한다.
4. 변환기가 `Curriculum`을 돌려주게 한다. `SourceDocument`를 만들고 포트와 어댑터를 바꾼다.
5. 접수 흐름(`SourceFindDataHandler`, `Deployment`, 도메인 서비스, 매퍼)에서 `SourceContentData`를 걷어내고 지운다.
6. 0~6장과 8장을 바뀐 코드에 맞게 고친다.

### 7.9 이번에 하지 않는 것

- **`SourceConfig`를 원본 뒤로 숨기기.** 지금은 배포의 애플리케이션 계층이 `SourceConfig`를 직접 꺼낸다. 비밀번호가 든 객체가 배포 쪽까지 온다. 컨텍스트 맵대로라면 배포는 "원본 저장소 ID와 문서 ID를 주면 `Curriculum`을 준다"는 포트 하나만 알아야 한다. 범위가 커서 따로 한다.
- **`SourceConfig`의 ID가 비어 있는 것.** `SourceDataAccessMapper.entityToSourceConfig`가 `id`를 옮기지 않는다. 위 작업 때 함께 고친다.

## 8. 모듈 지도

| 모듈 | 이 흐름의 클래스 | 역할 |
|---|---|---|
| `deployment-application` | `DeploymentController`, `DeploymentGlobalExceptionHandler` | REST 입구, 예외를 응답으로 |
| `deployment-application-service` | `DeploymentApplicationServiceImpl`, `SourceFindDataHandler`, `SourceConfigHelper`, `SourceFindHelper`, `DeploymentDataConvertHelper`, `CurriculumSourceConverter`, 포트 `SourceConfigRepository`, `SourceRepository` | 유스케이스 조율, 원본 문서 해석 |
| `deployment-domain-core` | `SourceConfig`, `SourceContentData`, `ChunkContent`, `SourceType`, `DbType`, `CurriculumBaseKey` | 프레임워크 없는 모델 |
| `deployment-dataaccess` | `SourceConfigRepositoryImpl`, `SourceRepositoryImpl`, `DynamicDataSourceConnector`, `MongoConnectHelper`, `MysqlConnectHelper`, `SourceDataAccessMapper` | 포트 구현, 원본 DB 접속 |

application-service는 포트(`SourceRepository`)만 알고, Mongo와 MySQL은 dataaccess 안에만 있다. 원본 칸 이름은 application-service의 `CurriculumSourceConverter`만 안다.
