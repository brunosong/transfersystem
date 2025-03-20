package com.brunosong.transfer.system.main;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class LearningMaterialDataGenerator {

    public static String connectionString = "mongodb://root:example@localhost:27017/exampledb?authSource=admin";

    public static void main(String[] args) {

        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            MongoDatabase database = mongoClient.getDatabase("learningDB");
            MongoCollection<Document> collection = database.getCollection("CurriculumData");

            collection.drop();

            generateAndInsertCurriculumData(collection);

            collection.createIndex(new Document("curriculum.curriculumId", 1));
            System.out.println("300개 커리큘럼, 각 10MB 데이터 삽입 완료!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void generateAndInsertCurriculumData(MongoCollection<Document> collection) {
        int curriculumCount = 300; // 300개 커리큘럼
        int gradeCount = 4; // 커리큘럼당 4개 학년
        int semesterCount = 2; // 학년당 2개 학기
        int subjectCount = 5; // 학기당 5개 과목
        int unitCount = 5; // 과목당 5개 단원
        int lessonCount = 100; // 단원당 100개 차시
        Random random = new Random();
        List<Document> documents = new ArrayList<>();

        String[] curriculumTitles = {"초등 교육 과정", "중등 교육 과정", "고등 교육 과정", "성인 기초 과정", "특수 교육 과정"};
        String[] gradePrefixes = {"초등", "중등", "고등", "성인"};
        String[] subjects = {"국어", "수학", "영어", "과학", "사회"};
        String[] formats = {"Video", "PDF", "Quiz", "Interactive", "Audio"};
        String[] languages = {"Korean", "English", "Bilingual"};

        for (int currIdx = 1; currIdx <= curriculumCount; currIdx++) {
            String curriculumId = "CUR" + String.format("%03d", currIdx);
            String curriculumTitle = curriculumTitles[currIdx % curriculumTitles.length] + " " + currIdx;
            List<Document> grades = new ArrayList<>();

            for (int gradeIdx = 1; gradeIdx <= gradeCount; gradeIdx++) {
                String gradeId = "G" + String.format("%04d", (currIdx * 100 + gradeIdx));
                String gradeTitle = gradePrefixes[currIdx % 4] + " " + gradeIdx + "학년";
                List<Document> semesters = new ArrayList<>();

                for (int semesterIdx = 1; semesterIdx <= semesterCount; semesterIdx++) {
                    String semesterId = "S" + String.format("%05d", (currIdx * 1000 + gradeIdx * 100 + semesterIdx));
                    String semesterTitle = semesterIdx + "학기";
                    List<Document> subjectList = new ArrayList<>();

                    for (int subjectIdx = 0; subjectIdx < subjectCount; subjectIdx++) {
                        String subjectId = "SUB" + String.format("%06d", (currIdx * 10000 + gradeIdx * 1000 + semesterIdx * 100 + subjectIdx));
                        String subjectTitle = subjects[subjectIdx];
                        List<Document> unitList = new ArrayList<>();

                        for (int unitIdx = 1; unitIdx <= unitCount; unitIdx++) {
                            String unitId = "U" + String.format("%07d", (currIdx * 100000 + gradeIdx * 10000 + semesterIdx * 1000 + subjectIdx * 100 + unitIdx));
                            List<Document> lessonList = new ArrayList<>();

                            for (int lessonIdx = 1; lessonIdx <= lessonCount; lessonIdx++) {
                                String lessonId = "L" + String.format("%08d", (currIdx * 1000000L + gradeIdx * 100000 + semesterIdx * 10000 + subjectIdx * 1000 + unitIdx * 100 + lessonIdx));
                                lessonList.add(new Document("id", lessonId)
                                        .append("title", "차시 " + lessonIdx + ": " + subjectTitle + " 학습 " + lessonIdx)
                                        .append("description", subjectTitle + " 차시 " + lessonIdx + " 내용")
                                        .append("lessonNumber", lessonIdx)
                                        .append("learningLevel", random.nextInt(3) + 1));

                            }

                            unitList.add(new Document("id", unitId)
                                    .append("title", "단원 " + unitIdx + ": " + subjectTitle + " 주제 " + unitIdx)
                                    .append("description", subjectTitle + " 단원 " + unitIdx)
                                    .append("lesson", lessonList));
                        }

                        subjectList.add(new Document("id", subjectId)
                                .append("title", subjectTitle)
                                .append("description", subjectTitle + " 학습")
                                .append("unit", unitList));
                    }

                    semesters.add(new Document("id", semesterId)
                            .append("title", semesterTitle)
                            .append("description", gradeTitle + " " + semesterTitle)
                            .append("subject", subjectList));
                }

                grades.add(new Document("id", gradeId)
                        .append("title", gradeTitle)
                        .append("description", gradeTitle + " 과정")
                        .append("semester", semesters));
            }

            // 단일 커리큘럼 다큐먼트 생성
            Document doc = new Document("curriculum", new Document("curriculumId", curriculumId)
                    .append("title", curriculumTitle)
                    .append("description", curriculumTitle + " 전체 커리큘럼")
                    .append("grade", grades));

            documents.add(doc);

            // 커리큘럼 단위로 삽입
            if (documents.size() >= 1) { // 300개라 바로 삽입
                collection.insertMany(documents);
                System.out.println("Inserted: " + curriculumId + " (~10MB)");
                documents.clear();
            }
        }

        if (!documents.isEmpty()) {
            collection.insertMany(documents);
        }
    }
}
