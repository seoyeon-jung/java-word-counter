package kr.sesac.wordcounter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WordCounter {

    public static void countWords(String text, Map<String, Long> wordCounts) {
        List<String> words = extractWords(text);

        for (String word : words) {
            wordCounts.put(word, wordCounts.getOrDefault(word, 0L) + 1L);
        }
    }

    public static List<String> extractWords(String text) {
        List<String> result = new ArrayList<>();

        String[] words = text.split("[^a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ]+");

        for (String word : words) {

            if (word.isEmpty()) {
                continue;
            }

            if (word.matches("\\d+")) {
                continue;
            }

            result.add(
                    word.toLowerCase()
            );
        }

        return result;
    }
}
