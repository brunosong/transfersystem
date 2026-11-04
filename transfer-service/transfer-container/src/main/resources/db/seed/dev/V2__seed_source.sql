-- k8s dev 환경에서 쓰는 원본 저장소
INSERT INTO sourceconfig (source_type, db_type, host, port, database, username, password, table_or_collection)
VALUES ('CURRICULUM', 'MONGO', 'mongodb-0.mongodb-headless.transfer.svc.cluster.local', 27017, 'learningDB', 'transfer_mongo_learning_user', 'asdf1234', 'CurriculumData');