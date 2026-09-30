package kr.sesac.wordcounter.analysis;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class WordCountSorter {

    private static final Comparator<Map.Entry<String, Long>> COMPARATOR =
            Comparator
                    .<Map.Entry<String, Long>, Long>comparing(
                            Map.Entry::getValue
                    )
                    .reversed()
                    .thenComparing(Map.Entry::getKey);

    private WordCountSorter() {
    }

    public static List<Map.Entry<String, Long>> sortedEntries(
            Map<String, Long> wordCounts
    ) {
        List<Map.Entry<String, Long>> entries =
                new ArrayList<>(wordCounts.entrySet());

        entries.sort(COMPARATOR);

        return entries;
    }
}