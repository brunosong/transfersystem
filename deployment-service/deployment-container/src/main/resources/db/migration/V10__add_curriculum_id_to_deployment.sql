-- 배포가 어느 커리큘럼을 보내는지 기억한다.
-- 과목은 뼈대 결과가 온 뒤에 아웃박스에 넣는데, 그때 메시지와 Kafka 키에 커리큘럼 ID가 필요하다.
-- 이미 쌓인 배포는 값을 채울 수 없어(원본에만 있다) NULL 을 허용한다. 새 배포는 늘 채운다.
ALTER TABLE deployment ADD COLUMN curriculum_id VARCHAR(100);