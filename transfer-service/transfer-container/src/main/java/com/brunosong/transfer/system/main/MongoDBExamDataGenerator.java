package com.brunosong.transfer.system.main;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MongoDBExamDataGenerator {

    public static String connectionString = "mongodb://root:example@localhost:27017/exampledb?authSource=admin";

    public static void main(String[] args) {

        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            MongoDatabase database = mongoClient.getDatabase("examDB");
            MongoCollection<Document> collection = database.getCollection("StudentDailyResults");

            collection.drop();

            // 인덱스 생성
            collection.createIndex(new Document("studentId", 1).append("date", 1));

            generateAndInsertDailyData(collection);
            System.out.println("300명 학생, 2000문제 데이터 삽입 완료!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void generateAndInsertDailyData(MongoCollection<Document> collection) {
        int studentCount = 500; // 300명 학생
        int days = 90; // 30일
        int totalQuestions = 10000; // 학생당 2000문제
        int questionsPerPaper = 20; // 시험지당 20문제
        int totalExams = totalQuestions / questionsPerPaper; // 100개 시험지
        int minExamsPerDay = totalExams / days; // 하루 최소 3.33개 → 3~4개로 조정
        Random random = new Random();
        List<Document> dailyDocs = new ArrayList<>();

        LocalDate startDate = LocalDate.of(2025, 3, 1);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

        for (int studentId = 1; studentId <= studentCount; studentId++) {
            String studentKey = "S" + String.format("%03d", studentId);
            int remainingQuestions = totalQuestions; // 남은 문제 수 추적

            for (int day = 0; day < days; day++) {
                String date = startDate.plusDays(day).format(formatter);
                Document dailyDoc = new Document("studentId", studentKey)
                        .append("date", date);

                // 하루 시험지 수 (남은 문제를 고려해 분배)
                int examsToday;
                if (day == days - 1) {
                    // 마지막 날: 남은 모든 문제를 소진
                    examsToday = remainingQuestions / questionsPerPaper;
                } else {
                    // 최소 3개, 최대 4개 랜덤 분배
                    examsToday = Math.min(minExamsPerDay + random.nextInt(2), remainingQuestions / questionsPerPaper);
                }
                if (examsToday <= 0) break; // 남은 문제가 없으면 종료

                List<Document> examResults = new ArrayList<>();
                int dailyTotalScore = 0;

                for (int examIdx = 0; examIdx < examsToday; examIdx++) {
                    String examPaperId = "E" + String.format("%03d", random.nextInt(200) + 1);
                    int examScore = 0;
                    int correctCount = 0;
                    String timestamp = Instant.now().minusSeconds((days - day) * 86400 - examIdx * 3600).toString();

                    // 문제 데이터 생성
                    List<Document> questions = new ArrayList<>();
                    for (int q = 1; q <= questionsPerPaper; q++) {
                        boolean isCorrect = random.nextBoolean();
                        int timeTaken = random.nextInt(300) + 10; // 10~309초
                        int score = isCorrect ? random.nextInt(10) + 1 : 0;

                        Document questionDoc = new Document("questionNumber", q)
                                .append("isCorrect", isCorrect)
                                .append("timeTaken", timeTaken)
                                .append("score", score);
                        questions.add(questionDoc);

                        examScore += score;
                        if (isCorrect) correctCount++;
                    }

                    Document examDoc = new Document("examPaperId", examPaperId)
                            .append("totalScore", examScore)
                            .append("correctCount", correctCount)
                            .append("timestamp", timestamp)
                            .append("questions", questions);
                    examResults.add(examDoc);
                    dailyTotalScore += examScore;
                    remainingQuestions -= questionsPerPaper;
                }

                dailyDoc.append("examResults", examResults)
                        .append("dailyTotalScore", dailyTotalScore)
                        .append("lastUpdated", Instant.now().minusSeconds((days - day - 1) * 86400).toString());
                dailyDocs.add(dailyDoc);

                if (dailyDocs.size() >= 1000) {
                    collection.insertMany(dailyDocs);
                    dailyDocs.clear();
                }
            }
        }

        if (!dailyDocs.isEmpty()) collection.insertMany(dailyDocs);
    }
}
