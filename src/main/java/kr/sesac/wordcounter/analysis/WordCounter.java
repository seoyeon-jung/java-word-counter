package kr.sesac.wordcounter.analysis;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class WordCounter {

    private static final String DELIMITER_REGEX = "[^a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ]+";
    private static final String NUMBER_REGEX = "\\d+";

    private WordCounter() {}

    public static void countWords(String text, Map<String, Long> wordCounts) {
        for (String word : extractWords(text)) {
            wordCounts.put(word, wordCounts.merge(word, 1L, Long::sum));
        }
    }

    public static List<String> extractWords(String text) {
        List<String> result = new ArrayList<>();

        String[] words = text.split(DELIMITER_REGEX);

        for (String word : words) {

            if (word.isEmpty() || word.matches(NUMBER_REGEX)) {
                continue;
            }

            result.add(word.toLowerCase(Locale.ROOT));
        }

        return result;
    }
}
