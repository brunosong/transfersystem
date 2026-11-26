-- 커리큘럼 원본을 저작 서비스의 공개본(published_curricula)으로 바꾼다(docs/system/authoring.md 2절).
-- 문서 모양이 같아서 배포 코드는 그대로다. 옛 CurriculumData 의 커리큘럼은 배포 화면에서 보이지 않게 된다
UPDATE sourceconfig
   SET table_or_collection = 'published_curricula'
 WHERE source_type = 'CURRICULUM'
   AND table_or_collection = 'CurriculumData';
