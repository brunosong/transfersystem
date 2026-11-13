-- 아웃박스 행에 Kafka 메시지 키를 저장한다.
-- 키는 "대상 시스템:환경:커리큘럼 ID"다(도메인 모델 7절). 같은 커리큘럼을 같은 대상에 보내는 명령은
-- 배포가 달라도 모두 같은 파티션에 들어가 넣은 순서대로 처리된다.
-- 전에는 행마다 무작위 값이 키라 같은 배포의 청크가 파티션으로 흩어졌다.
-- 꺼내는 쪽에서 "같은 키의 앞 행이 실패하면 뒤 행을 멈춘다"를 하려면 SQL로 키를 볼 수 있어야 해서 칸으로 둔다(ADR 0004).
ALTER TABLE deployment_outbox ADD COLUMN message_key VARCHAR(200);

-- 이미 쌓인 행은 페이로드에서 채운다
UPDATE deployment_outbox
   SET message_key = (payload ->> 'targetSystem') || ':' || (payload ->> 'environment') || ':' || (payload ->> 'curriculumId');

ALTER TABLE deployment_outbox ALTER COLUMN message_key SET NOT NULL;
