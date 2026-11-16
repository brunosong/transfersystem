-- 아웃박스의 version 칸을 지운다.
-- 처음에는 낙관적 락에 쓰려고 만들었지만 값을 넣거나 읽는 곳이 없다.
-- 행마다 상태를 고치는 것은 스케줄러 하나뿐이고, 여러 대일 때는 어드바이저리 락으로 한 대만 발행한다(ADR 0004).
-- 배포 표(deployment)의 version 은 결과 처리의 낙관적 락에 쓰므로 그대로 둔다.
ALTER TABLE deployment_outbox DROP COLUMN version;