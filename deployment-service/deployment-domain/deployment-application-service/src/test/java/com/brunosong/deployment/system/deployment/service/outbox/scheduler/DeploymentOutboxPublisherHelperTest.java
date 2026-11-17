package com.brunosong.deployment.system.deployment.service.outbox.scheduler;

import com.brunosong.deployment.system.deployment.service.outbox.model.DeploymentEventPayload;
import com.brunosong.deployment.system.deployment.service.outbox.model.DeploymentOutboxMessage;
import com.brunosong.deployment.system.deployment.service.ports.output.message.publisher.DeploymentRequestMessagePublisher;
import com.brunosong.deployment.system.deployment.service.ports.output.message.publisher.PublishResult;
import com.brunosong.deployment.system.deployment.service.ports.output.repository.ChunkPayload;
import com.brunosong.deployment.system.deployment.service.ports.output.repository.ChunkPayloadRepository;
import com.brunosong.deployment.system.deployment.service.valueobject.ChunkContent;
import com.brunosong.deployment.system.deployment.service.valueobject.TransType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessResourceFailureException;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * 아웃박스 행 하나를 보낼 때, 행에서 무엇을 보낼지 읽고 내용 표에서 내용을 꺼내 발행 포트에 넘기는지 본다.
 * 발행 포트(Kafka)와 내용 표(DB)는 가짜로 둔다. 실제 Kafka 전송은 이 테스트의 관심이 아니다.
 */
class DeploymentOutboxPublisherHelperTest {

    private static final UUID DEPLOYMENT_ID = UUID.fromString("6f1d9f5e-7f4b-4c55-9a35-3c2b0f6a1d11");

    // 가짜 발행 포트. 무엇을 받았는지 verify 로 확인한다
    private final DeploymentRequestMessagePublisher publisher = mock(DeploymentRequestMessagePublisher.class);
    // 가짜 내용 표. 테스트마다 when 으로 돌려줄 값을 정한다
    private final ChunkPayloadRepository chunkPayloadRepository = mock(ChunkPayloadRepository.class);
    // 진짜 ObjectMapper. 행의 JSON 을 실제로 읽는지 보려고 가짜로 두지 않는다.
    // createdAt 은 넣지 않는다. 이 모듈의 테스트에는 시간 모듈이 없다(실행할 때는 스프링의 ObjectMapper 가 다룬다)
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final DeploymentOutboxPublisherHelper helper =
            new DeploymentOutboxPublisherHelper(publisher, chunkPayloadRepository, objectMapper);

    private final ChunkContent.Skeleton skeleton = new ChunkContent.Skeleton("중등", "중등 과정", List.of());

    @Test
    @DisplayName("행이 가리키는 배포와 청크 번호로 내용 표에서 내용을 꺼내 발행 포트에 넘긴다")
    void readsContentFromPayloadTable() throws Exception {
        // 확인하는 것: 발행할 내용이 행이 아니라 내용 표에서 온다

        // 준비: 0번 청크(뼈대)를 가리키는 행. 내용 표는 그 번호로 물으면 뼈대를 돌려준다
        DeploymentOutboxMessage row = row(objectMapper.writeValueAsString(payload(0)));
        when(chunkPayloadRepository.findAll(DEPLOYMENT_ID, List.of(0)))
                .thenReturn(List.of(new ChunkPayload(0, skeleton)));
        // 발행 포트는 무엇을 받든 보냈다고 답한다
        when(publisher.publish(any(), any(), any())).thenReturn(PublishResult.PUBLISHED);

        // 실행
        PublishResult result = helper.publish(row);

        // 확인: 포트의 결과를 그대로 돌려주고, 포트에는 내용 표에서 꺼낸 뼈대가 넘어갔다
        assertThat(result).isEqualTo(PublishResult.PUBLISHED);
        // 첫 인자는 행 그대로, 둘째는 행에서 읽은 페이로드(아무거나), 셋째가 내용 표의 뼈대인지 본다
        verify(publisher).publish(eq(row), any(DeploymentEventPayload.class), eq(skeleton));
    }

    @Test
    @DisplayName("내용 표에 내용이 없으면 보내지 않고 포기한다")
    void missingContentGivesUp() throws Exception {
        // 확인하는 것: 내용이 지워진 청크는 다시 읽어도 없으므로 행을 포기로 닫는다

        // 준비: 내용 표가 빈 목록을 돌려준다
        DeploymentOutboxMessage row = row(objectMapper.writeValueAsString(payload(1)));
        when(chunkPayloadRepository.findAll(DEPLOYMENT_ID, List.of(1))).thenReturn(List.of());

        // 실행 + 확인
        assertThat(helper.publish(row)).isEqualTo(PublishResult.GIVE_UP);
        // 발행 포트는 한 번도 불리지 않았다
        verifyNoInteractions(publisher);
    }

    @Test
    @DisplayName("내용 표를 읽다가 DB 오류가 나면 다음 주기에 다시 보낸다")
    void dbErrorRetriesLater() throws Exception {
        // 확인하는 것: DB 가 잠깐 안 되는 것은 다시 하면 될 수 있으므로 행을 닫지 않는다

        // 준비: 내용 표가 스프링의 DB 접근 예외를 던진다
        DeploymentOutboxMessage row = row(objectMapper.writeValueAsString(payload(0)));
        when(chunkPayloadRepository.findAll(any(), any()))
                .thenThrow(new DataAccessResourceFailureException("연결이 끊겼다"));

        // 실행 + 확인
        assertThat(helper.publish(row)).isEqualTo(PublishResult.RETRY_LATER);
        verifyNoInteractions(publisher);
    }

    @Test
    @DisplayName("행의 페이로드를 못 읽으면 내용 표도 Kafka 도 건드리지 않고 포기한다")
    void unreadablePayloadGivesUp() {
        // 확인하는 것: 행의 JSON 이 깨져 있으면 다시 읽어도 같으므로 포기한다

        // 준비: JSON 이 아닌 페이로드
        DeploymentOutboxMessage row = row("JSON 이 아님");

        // 실행 + 확인
        assertThat(helper.publish(row)).isEqualTo(PublishResult.GIVE_UP);
        // 어느 쪽에도 가지 않았다
        verifyNoInteractions(chunkPayloadRepository, publisher);
    }

    private DeploymentEventPayload payload(int chunkNo) {
        return DeploymentEventPayload.builder()
                .deploymentId(DEPLOYMENT_ID)
                .targetSystem("ONLINE_CAMPUS")
                .environment("DEV")
                .transType(TransType.MESSAGING)
                .curriculumId("CUR001")
                .chunkOffset(chunkNo)
                .build();
    }

    private DeploymentOutboxMessage row(String payloadJson) {
        return DeploymentOutboxMessage.builder()
                .id(UUID.randomUUID())
                .transType(TransType.MESSAGING)
                .messageKey("ONLINE_CAMPUS:DEV:CUR001")
                .payload(payloadJson)
                .build();
    }
}
