package com.brunosong.transfer.system.main;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MongoDBExamDataGenerator {

    public static void main(String[] args) {
        // MongoDB 연결 설정
        String connectionString = "mongodb://root:example@localhost:27017/exampledb?authSource=admin";
        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            MongoDatabase database = mongoClient.getDatabase("exampledb");
            MongoCollection<Document> collection = database.getCollection("StudentExamResult");

            // 데이터 생성 및 삽입
            generateAndInsertExamData(collection);
            System.out.println("데이터 삽입 완료!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void generateAndInsertExamData(MongoCollection<Document> collection) {
        int studentCount = 300;
        int examPaperCount = 200; // 총 4000문제 / 20 = 200 시험지
        int questionsPerPaper = 20; // 시험지당 20문제
        Random random = new Random();
        List<Document> documents = new ArrayList<>();

        String[] difficulties = {"Easy", "Medium", "Hard"};
        String[] types = {"MultipleChoice", "ShortAnswer", "TrueFalse"};

        // 각 시험지의 문제별 점수를 미리 정의 (랜덤으로 1~10점)
        int[] questionScores = new int[questionsPerPaper];
        for (int i = 0; i < questionsPerPaper; i++) {
            questionScores[i] = random.nextInt(10) + 1; // 1~10점
        }

        // 300명의 학생이 200개의 시험지를 풂
        for (int studentId = 1; studentId <= studentCount; studentId++) {
            for (int examPaperId = 1; examPaperId <= examPaperCount; examPaperId++) {
                for (int questionNumber = 1; questionNumber <= questionsPerPaper; questionNumber++) {
                    boolean isCorrect = random.nextBoolean(); // 정답 여부
                    int timeTaken = random.nextInt(300) + 10; // 소요 시간 (10~309초)
                    int score = isCorrect ? questionScores[questionNumber - 1] : 0; // 정답이면 문제 점수, 오답이면 0
                    String difficulty = difficulties[random.nextInt(difficulties.length)];
                    String type = types[random.nextInt(types.length)];

                    Document doc = new Document("studentId", "S" + String.format("%03d", studentId)) // S001 형식
                            .append("examPaperId", "E" + String.format("%03d", examPaperId)) // E001 형식
                            .append("questionNumber", questionNumber) // 1~20
                            .append("isCorrect", isCorrect)
                            .append("timeTaken", timeTaken)
                            .append("score", score)
                            .append("difficulty", difficulty)
                            .append("type", type);

                    documents.add(doc);

                    // 1000개 단위로 삽입
                    if (documents.size() >= 1000) {
                        collection.insertMany(documents);
                        documents.clear();
                        System.out.println("Student " + studentId + " - ExamPaper " + examPaperId + " - Question " + questionNumber + " inserted");
                    }
                }
            }
        }

        // 남은 데이터 삽입
        if (!documents.isEmpty()) {
            collection.insertMany(documents);
        }
    }
}
