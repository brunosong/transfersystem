-- 배포 상태를 새 상태 기계(IN_PROGRESS, SUCCEEDED, FAILED)로 줄였다(docs/domain/model.md 4장).
-- 옛 처리 단계 값으로 남은 행을 새 값으로 바꾼다. 바꾸지 않으면 코드가 이 행을 읽지 못한다.
--   SUCCESS                   -> SUCCEEDED : 옛 길에서 끝까지 성공한 배포
--   PENDING, SENT, PROCESSED  -> FAILED    : 옛 길에서 끝나지 않은 배포. 청크 표(deployment_chunk) 행이 없어
--                                            결과가 와도 이어 받을 수 없으므로 끝난 것으로 둔다. 다시 보내려면 재배포한다
UPDATE deployment SET deployment_status = 'SUCCEEDED' WHERE deployment_status = 'SUCCESS';
UPDATE deployment
   SET deployment_status = 'FAILED',
       result_message    = COALESCE(result_message, '옛 처리 방식에서 끝나지 않은 배포라 실패로 닫았습니다(V13)')
 WHERE deployment_status IN ('PENDING', 'SENT', 'PROCESSED');
