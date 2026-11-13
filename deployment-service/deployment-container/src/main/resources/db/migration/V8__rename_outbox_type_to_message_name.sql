-- 아웃박스 type 칸의 값을 사가 이름에서 메시지 종류 이름으로 바꾼다.
-- type 은 "이 행이 어떤 메시지인가"를 적는 칸이다. 사가는 쓰지 않기로 해서(ADR 0003) 사가 이름이 남을 이유가 없다.
-- 사가를 붙일 때는 사가 상태를 아웃박스가 아니라 사가 쪽에 따로 둔다.
UPDATE deployment_outbox
   SET type = 'ApplyChunkCommand'
 WHERE type = 'deploymentProcessingSaga';
