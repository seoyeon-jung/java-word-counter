package kr.sesac.wordcounter;

import java.util.Map;

public class WordCounter {

    public static void countWords(String text, Map<String, Integer> wordCounts) {
        String[] words = text.split("[^a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ]+");

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
