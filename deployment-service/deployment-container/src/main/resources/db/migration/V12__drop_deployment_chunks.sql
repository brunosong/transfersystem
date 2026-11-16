-- 옛 결과 표를 지운다.
-- 결과가 와야 행이 생겨 "아직 안 보냄"과 "응답 없음"을 구별할 수 없었다(V5 참고).
-- 이제 결과는 청크 표(deployment_chunk)에만 적고, 목록과 상세 화면도 그 표를 읽는다.
DROP TABLE IF EXISTS deployment_chunks;
