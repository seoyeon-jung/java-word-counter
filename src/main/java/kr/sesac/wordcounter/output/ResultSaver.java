package kr.sesac.wordcounter.output;

import kr.sesac.wordcounter.analysis.WordCountSorter;
import kr.sesac.wordcounter.model.AnalysisResult;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class ResultSaver {

    private static final Path OUTPUT_PATH = Path.of("out", "counts.tsv");

    private ResultSaver() {}

    public static Path save(AnalysisResult result) throws IOException {
        Files.createDirectories(OUTPUT_PATH.getParent());

        List<Map.Entry<String, Long>> entries = WordCountSorter.sortedEntries(result.getWordCounts());

        try (BufferedWriter writer = Files.newBufferedWriter(OUTPUT_PATH, StandardCharsets.UTF_8)) {

            writer.write("word\tcount");
            writer.newLine();

            for (Map.Entry<String, Long> entry : entries) {
                writer.write(entry.getKey() + "\t" + entry.getValue());
                writer.newLine();
            }
        }

        return OUTPUT_PATH;
    }
}