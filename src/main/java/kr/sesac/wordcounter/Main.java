package kr.sesac.wordcounter;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Main {

    private static final String CSV_COLUMN = "text";
    private static final String TSV_COLUMN = "document";

    public static void main(String[] args) throws IOException {

        // 테스트할 파일
        Path input = Path.of("samples/equivalent/basic.tsv");

        System.out.println("문서 단어 분석기");
        System.out.println("입력 파일: " + input);
        System.out.println();

        // 단어별 출현 횟수 저장
        Map<String, Integer> wordCounts = new HashMap<>();

        String fileName = input.getFileName().toString().toLowerCase();

        // TXT 파일 처리
        if (fileName.endsWith(".txt")) {

            try (BufferedReader reader =
                         Files.newBufferedReader(input, StandardCharsets.UTF_8)) {

                String line;

                while ((line = reader.readLine()) != null) {

                    String[] words =
                            line.split("[^a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ]+");

                    for (String word : words) {

                        if (word.isEmpty()) {
                            continue;
                        }

                        if (word.matches("\\d+")) {
                            continue;
                        }

                        word = word.toLowerCase();

                        wordCounts.put(
                                word,
                                wordCounts.getOrDefault(word, 0) + 1
                        );
                    }
                }
            }
        }

        // CSV 파일 처리
        else if (fileName.endsWith(".csv")) {

            try (BufferedReader reader =
                         Files.newBufferedReader(input, StandardCharsets.UTF_8);

                 CSVParser parser =
                         CSVFormat.DEFAULT.builder()
                                 .setHeader()
                                 .setSkipHeaderRecord(true)
                                 .get()
                                 .parse(reader)) {

                for (CSVRecord record : parser) {

                    // CSV에서는 text 열만 분석
                    String text = record.get(CSV_COLUMN);

                    String[] words =
                            text.split("[^a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ]+");

                    for (String word : words) {

                        if (word.isEmpty()) {
                            continue;
                        }

                        if (word.matches("\\d+")) {
                            continue;
                        }

                        word = word.toLowerCase();

                        wordCounts.put(
                                word,
                                wordCounts.getOrDefault(word, 0) + 1
                        );
                    }
                }
            }
        }

        // tsv 파일 처리
        else if (fileName.endsWith(".tsv")) {
            try (BufferedReader reader =
                         Files.newBufferedReader(input, StandardCharsets.UTF_8);

                 CSVParser parser =
                         CSVFormat.DEFAULT.builder()
                                 // TSV는 쉼표가 아니라 탭으로 열을 구분
                                 .setDelimiter('\t')
                                 .setHeader()
                                 .setSkipHeaderRecord(true)
                                 .get()
                                 .parse(reader)) {

                for (CSVRecord record : parser) {

                    // TSV에서는 document 열만 분석
                    String document = record.get(TSV_COLUMN);

                    String[] words =
                            document.split("[^a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ]+");

                    for (String word : words) {

                        if (word.isEmpty()) {
                            continue;
                        }

                        // 숫자로만 이루어진 단어 제외
                        if (word.matches("\\d+")) {
                            continue;
                        }

                        // 영문 소문자 변환
                        word = word.toLowerCase();

                        // 단어별 횟수 증가
                        wordCounts.put(
                                word,
                                wordCounts.getOrDefault(word, 0) + 1
                        );
                    }
                }
            }
        }

        else {
            System.out.println("현재 지원하지 않는 파일 형식입니다.");
            return;
        }

        // 전체 단어 수 계산
        int totalCount = 0;

        for (int count : wordCounts.values()) {
            totalCount += count;
        }

        // 결과 출력
        System.out.println("전체 단어: " + totalCount + "개");
        System.out.println("서로 다른 단어: " + wordCounts.size() + "개");

        for (Map.Entry<String, Integer> entry : wordCounts.entrySet()) {
            System.out.println(
                    entry.getKey() + " = " + entry.getValue()
            );
        }
    }
}