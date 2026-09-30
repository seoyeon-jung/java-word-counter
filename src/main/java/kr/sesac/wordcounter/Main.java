package kr.sesac.wordcounter;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {
        Path input = Path.of("samples/equivalent/basic.txt");

        System.out.println("문서 단어 분석기 - 시작 코드");
        System.out.println("입력 파일: " + input);
        System.out.println();

        Map<String, Integer> wordCounts = new HashMap<>();

        try (BufferedReader reader = Files.newBufferedReader(input, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split("[^a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ]+");

                for (String word : words) {
                    if (word.isEmpty()) {
                        continue;
                    }

                    if (word.matches("\\d+")) {
                        continue;
                    }

                    word = word.toLowerCase();

                    wordCounts.put(word, wordCounts.getOrDefault(word, 0) + 1);
                }
            }
        }

        int totalCount = 0;
        for (int count : wordCounts.values()) {
            totalCount += count;
        }

        System.out.println("전체 단어: " + totalCount + "개");
        System.out.println("서로 다른 단어: " + wordCounts.size() + "개");

        for (Map.Entry<String, Integer> entry : wordCounts.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
    }
}
}
