# ADR 0001. 메시지 계약(Avro 스키마)을 서비스에 나눠 주는 방식

- 상태: 채택 (B 방식)
- 관련 모듈: `infrastructure/message-contracts`, `*-messaging`

## 맥락

deployment-service와 data-migration-service는 Kafka로 Avro 메시지를 주고받는다. 양쪽이 같은 계약을 봐야 한다.

전에는 `kafka-model` 모듈이 스키마에서 만든 **클래스**까지 담고 있었고, 두 서비스가 그 클래스를 그대로 썼다. 그러면 두 서비스가 같은 스키마 버전에 묶인다. 생산자가 버전을 올리면 소비자도 함께 올려야 했다.

그래서 **나눠 갖는 것은 스키마(.avsc)로 하고, 클래스는 각 messaging 모듈이 자기 빌드에서 만들기로** 했다. 메시지에는 쓴 쪽의 스키마 ID가 실려 오고, 읽는 쪽은 자기 스키마로 맞춰 읽는다. 따라서 두 서비스가 서로 다른 계약 버전에 있어도 된다.

남은 질문은 "각 모듈이 스키마를 어디서 가져오는가"다.

## 선택지

| | 방식 | 장점 | 단점 |
|---|---|---|---|
| A | 저장소 안 계약 폴더를 상대 경로로 읽는다 | 가장 단순하다 | 같은 저장소일 때만 동작한다 |
| **B** | **계약 아티팩트(jar)에서 `.avsc`를 꺼낸다** | 버전이 박혀 재현된다. 넥서스는 이미 빌드가 의존하는 곳이라 새 장애점이 없다. 계약을 다른 저장소로 떼어도 그대로 쓴다 | 같은 리액터 안에서는 `mvn install`을 한 번 돌린 뒤에야 `mvn test`가 된다 |
| C | 스키마 레지스트리에서 내려받는다 | 원본을 저장소에 두지 않아도 된다 | 레지스트리가 빌드 의존성이 된다. 레지스트리가 죽으면 빌드가 멈추고, 버전을 고정하지 않으면 같은 커밋이 다른 결과를 낸다 |

## 결정

**B 방식**을 쓴다. 설정은 루트 `pom.xml`의 `pluginManagement`에 한 번만 둔다. 각 messaging 모듈은 두 플러그인(`maven-dependency-plugin`, `avro-maven-plugin`)을 선언만 한다.

- `maven-dependency-plugin:unpack`이 계약 jar의 `avro/*.avsc`를 `target/contracts`로 꺼낸다.
- `avro-maven-plugin`이 그 폴더에서 클래스를 만든다.
- 두 플러그인은 같은 `generate-sources` 단계에 걸려 있다. 같은 단계의 플러그인은 pom에 선언된 순서대로 실행되므로, 모듈 pom에서도 **unpack을 먼저** 선언해야 한다.

### 다른 방식으로 바꾸려면

**A로 되돌리기:** `avro-maven-plugin`의 `sourceDirectory`를 계약 폴더의 상대 경로로 바꾸고 unpack을 뺀다.
```xml
<sourceDirectory>${project.basedir}/../../infrastructure/message-contracts/src/main/resources/avro</sourceDirectory>
```

**C로 바꾸기:** unpack 대신 아래 플러그인을 선언하고, `avro-maven-plugin`의 `sourceDirectory`를 `${project.build.directory}/avro`로 바꾼다. 받은 파일은 subject 이름으로 저장된다. 하지만 클래스 이름은 파일명이 아니라 스키마 안의 namespace와 name으로 정해지므로 결과는 같다.
```xml
<plugin>
    <groupId>io.confluent</groupId>
    <artifactId>kafka-schema-registry-maven-plugin</artifactId>
    <version>${confluent.version}</version>
    <executions>
        <execution>
            <id>download-contracts</id>
            <phase>generate-sources</phase>
            <goals><goal>download</goal></goals>
            <configuration>
                <schemaRegistryUrls><param>${schema.registry.url}</param></schemaRegistryUrls>
                <outputDirectory>${project.build.directory}/avro</outputDirectory>
                <subjectPatterns>
                    <param>^com\.brunosong\.deployment\.contract\.ApplyChunkCommand$</param>
                    <param>^com\.brunosong\.deployment\.contract\.ApplyResult$</param>
                </subjectPatterns>
            </configuration>
        </execution>
    </executions>
</plugin>
```

## 스키마 호환성 검사와 등록

검사와 등록은 빌드 수명주기에 걸지 않았다. 걸어 두면 레지스트리가 떠 있어야 빌드가 되고, 레지스트리가 죽으면 CI가 멈춘다. 필요할 때 명시적으로 실행한다.

```bash
./mvnw -pl infrastructure/message-contracts io.confluent:kafka-schema-registry-maven-plugin:test-compatibility
./mvnw -pl infrastructure/message-contracts io.confluent:kafka-schema-registry-maven-plugin:register
```

PR에서는 `test-compatibility`로 소비자를 깨뜨리는 변경을 막고, 머지된 뒤에 `register`로 새 버전을 올린다.
