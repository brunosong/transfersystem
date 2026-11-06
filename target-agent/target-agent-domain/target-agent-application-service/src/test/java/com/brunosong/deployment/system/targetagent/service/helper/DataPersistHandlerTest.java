package com.brunosong.deployment.system.targetagent.service.helper;

import com.brunosong.deployment.system.targetagent.service.DataPersistService;
import com.brunosong.deployment.system.targetagent.service.domain.valueobject.AppliedContent;
import com.brunosong.deployment.system.targetagent.service.domain.valueobject.TargetSystemEnvironment;
import com.brunosong.deployment.system.targetagent.service.handler.DataPersistHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringJUnitConfig(
        classes = {
                BootCampDataPersistHelper.class,
                OnlineCampusDataPersistHelper.class,
                DataPersistHandler.class
        })
class DataPersistHandlerTest {

    @Autowired
    DataPersistHandler dataPersistHandler;

    @MockBean
    OnlineCampusDataPersistHelper onlineCampusDataPersistHelper;

    @Test
    @DisplayName("대상 시스템이 ONLINE_CAMPUS 이면 온라인캠퍼스 어댑터에 맡기고 반영 내용을 돌려준다")
    void testDataPersist() {
        // Given
        AppliedContent applied = new AppliedContent.Subject("SUB001", "수학", 5, 500);
        Mockito.when(onlineCampusDataPersistHelper.dataPersist(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(applied);

        // When
        AppliedContent result = dataPersistHandler.persist("ONLINE_CAMPUS", TargetSystemEnvironment.DEV, "CUR001", null);

        // Then
        assertEquals(applied, result);
        Mockito.verify(onlineCampusDataPersistHelper, Mockito.times(1))
                .dataPersist(TargetSystemEnvironment.DEV, "CUR001", null);
    }

    @Test
    @DisplayName("어댑터가 프록시로 감싸져 있어도 대상 시스템을 찾는다")
    void testSupportsProxiedAdapter() {
        // @Transactional 때문에 실행 환경에서는 어댑터가 CGLIB 프록시가 된다. Mock 으로는 이 상황이 재현되지 않는다
        ProxyFactory factory = new ProxyFactory(new OnlineCampusDataPersistHelper(null, null));
        factory.setProxyTargetClass(true);
        DataPersistService proxied = (DataPersistService) factory.getProxy();

        assertTrue(new DataPersistHandler(List.of(proxied)).supports("ONLINE_CAMPUS"));
    }

    @Test
    @DisplayName("맡지 않은 대상 시스템은 지원하지 않는다고 답한다")
    void testSupports() {
        assertTrue(dataPersistHandler.supports("BOOTCAMP"));
        assertFalse(dataPersistHandler.supports("UNKNOWN"));
    }

    @Test
    @DisplayName("부트캠프는 반영하지 않았으므로 성공으로 알리지 않는다")
    void testBootcampIsNotReportedAsApplied() {
        assertThrows(UnsupportedOperationException.class,
                () -> dataPersistHandler.persist("BOOTCAMP", TargetSystemEnvironment.DEV, "CUR001", null));
    }
}