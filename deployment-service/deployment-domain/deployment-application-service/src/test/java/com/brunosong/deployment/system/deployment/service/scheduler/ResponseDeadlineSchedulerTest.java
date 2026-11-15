package com.brunosong.deployment.system.deployment.service.scheduler;

import com.brunosong.deployment.system.deployment.service.handler.DeploymentDeadlineHandler;
import com.brunosong.deployment.system.deployment.service.ports.output.repository.DeploymentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * 응답 기한 스케줄러가 "무엇을 찾고, 누구에게 맡기는지"만 본다.
 *
 * 스케줄러는 직접 배포를 고치지 않는다. 할 일은 둘뿐이다.
 *   1. 저장소에 "기한이 지난 SENT 청크가 있는 배포 ID"를 묻는다.
 *   2. 찾은 배포 ID 마다 핸들러에게 처리를 맡긴다(배포마다 트랜잭션 하나).
 *
 * 그래서 저장소와 핸들러를 둘 다 가짜(mock)로 두고,
 * "저장소에 어떤 시각을 넘겼나", "핸들러를 몇 번, 어떤 값으로 불렀나"를 확인한다.
 * 배포가 실제로 NO_REPLY 로 바뀌는 규칙은 도메인 테스트(DeploymentTest)와 핸들러 테스트가 본다.
 */
class ResponseDeadlineSchedulerTest {

    /** 테스트에서 쓰는 응답 기한. 운영 기본값(10분)과 같게 둔다 */
    private static final Duration DEADLINE = Duration.ofMinutes(10);

    // 가짜 저장소: 어떤 배포 ID를 돌려줄지 테스트가 정한다(when ... thenReturn)
    private final DeploymentRepository deploymentRepository = mock(DeploymentRepository.class);
    // 가짜 핸들러: 실제로 배포를 고치지 않고, 불렸는지만 기록한다(verify 로 확인)
    private final DeploymentDeadlineHandler handler = mock(DeploymentDeadlineHandler.class);
    // 시험할 대상. 진짜 스케줄러에 가짜 둘과 기한을 넣는다. @Scheduled 는 테스트에서 돌지 않으므로 메서드를 직접 부른다
    private final ResponseDeadlineScheduler scheduler = new ResponseDeadlineScheduler(deploymentRepository, handler, DEADLINE);

    /**
     * 확인하는 것: 스케줄러가 "지금 - 기한"을 기준 시각으로 저장소에 묻는다.
     *
     * 이 기준이 틀리면(예: 지금을 그대로 넘기면) 방금 보낸 청크까지 응답 없음으로 바뀐다.
     * 반대로 기한을 두 번 빼면 너무 늦게 바뀐다. 그래서 넘긴 시각을 붙잡아(ArgumentCaptor) 값을 본다.
     *
     * 스케줄러가 안에서 Instant.now() 를 부르므로 정확한 값을 알 수 없다.
     * 그래서 "부르기 직전의 지금 - 기한"과 "부른 직후의 지금 - 기한" 사이에 있는지로 확인한다.
     *
     * 덤으로, 찾은 배포가 없으면 핸들러를 한 번도 부르지 않는지도 본다.
     */
    @Test
    @DisplayName("지금에서 기한만큼 앞선 시각 전에 보낸 청크가 있는 배포를 찾는다")
    void looksForChunksSentBeforeDeadline() {
        // 준비: 저장소는 어떤 시각을 받든 빈 목록을 돌려준다(기한이 지난 배포가 없는 상황)
        when(deploymentRepository.findIdsWithChunksSentBefore(any())).thenReturn(List.of());
        Instant before = Instant.now();

        // 실행: 스케줄러 한 주기를 직접 돌린다
        scheduler.expireOverdueChunks();

        // 확인 1: 저장소에 넘긴 기준 시각을 붙잡아, "지금 - 10분" 범위 안인지 본다
        ArgumentCaptor<Instant> cutoff = ArgumentCaptor.forClass(Instant.class);
        verify(deploymentRepository).findIdsWithChunksSentBefore(cutoff.capture());
        assertThat(cutoff.getValue()).isBetween(before.minus(DEADLINE), Instant.now().minus(DEADLINE));

        // 확인 2: 찾은 배포가 없으니 핸들러는 한 번도 불리지 않는다
        verifyNoInteractions(handler);
    }

    /**
     * 확인하는 것: 배포 하나를 처리하다 예외가 나도, 다음 배포는 계속 처리한다.
     *
     * 실제로는 같은 배포의 결과 처리와 부딪혀 낙관적 락 예외가 날 수 있다
     * (스케줄러가 배포를 읽은 사이에 결과가 먼저 저장된 경우).
     * 이때 반복문이 멈추면, 뒤에 있는 배포들은 이번 주기에 아무도 처리하지 못한다.
     * 실패한 배포는 다음 주기에 다시 보면 되므로, 스케줄러는 로그만 남기고 넘어가야 한다.
     */
    @Test
    @DisplayName("한 배포가 실패해도 나머지 배포는 처리한다")
    void oneFailureDoesNotStopOthers() {
        // 준비: 기한이 지난 배포가 둘 있다
        UUID first = UUID.randomUUID();
        UUID second = UUID.randomUUID();
        when(deploymentRepository.findIdsWithChunksSentBefore(any())).thenReturn(List.of(first, second));
        // 준비: 첫 번째 배포를 처리할 때 핸들러가 예외를 던진다(낙관적 락 충돌을 흉내 낸다)
        doThrow(new IllegalStateException("낙관적 락 충돌")).when(handler).expireOverdue(eq(first), any(), any());

        // 실행: 예외가 스케줄러 밖으로 새어 나오면 테스트가 여기서 실패한다
        scheduler.expireOverdueChunks();

        // 확인: 첫 번째도 불렸고(그리고 실패했고), 두 번째도 불렸다. 둘 다 같은 기한(10분)을 넘겨받았다
        verify(handler).expireOverdue(eq(first), any(), eq(DEADLINE));
        verify(handler).expireOverdue(eq(second), any(), eq(DEADLINE));
    }
}
