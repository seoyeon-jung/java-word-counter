package kr.sesac.wordcounter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws IOException {

        // 테스트할 파일
        Path input = Path.of("samples/equivalent/basic.html");

        System.out.println("문서 단어 분석기");
        System.out.println("입력 파일: " + input);
        System.out.println();

        // 단어별 출현 횟수 저장
        Map<String, Integer> wordCounts = new HashMap<>();

        // 파일 분석
        FileParser.parse(input, wordCounts);

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