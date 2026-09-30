package kr.sesac.wordcounter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ResultSaver {

    private static final Path OUTPUT_PATH = Path.of("out", "counts.tsv");

    public static Path save(AnalysisResult result) throws IOException {
        Path parent = OUTPUT_PATH.getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        List<Map.Entry<String, Long>> entries = new ArrayList<>(result.getWordCounts().entrySet());
        entries.sort((e1, e2) -> {
            int countCompare = Long.compare(e2.getValue(), e1.getValue());

            if (countCompare == 0) {
                return countCompare;
            }

            return e1.getKey().compareTo(e2.getKey());
        });

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
