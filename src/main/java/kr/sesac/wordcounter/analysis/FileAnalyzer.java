package kr.sesac.wordcounter.analysis;

import kr.sesac.wordcounter.model.AnalysisConfig;
import kr.sesac.wordcounter.model.AnalysisResult;
import kr.sesac.wordcounter.parser.FileParser;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class FileAnalyzer {

    private FileAnalyzer() {}

    public static AnalysisResult analyze(Path input, AnalysisConfig config) throws IOException {

        if (!Files.exists(input)) {
            throw new IOException("경로를 찾을 수 없습니다: " + input);
        }

        List<Path> files = new ArrayList<>();
        int skippedFiles = collectFiles(input, files);

        Map<String, Long> totalWordCounts = new HashMap<>();

        int successFiles = 0;
        int failedFiles = 0;

        long startTime = System.nanoTime();

        for (Path file : files) {
            Map<String, Long> fileWordCounts = new HashMap<>();

            try {
                FileParser.parse(file, fileWordCounts, config);

                mergeCounts(totalWordCounts, fileWordCounts);

                successFiles++;

            } catch (IOException | UncheckedIOException | IllegalArgumentException e) {
                failedFiles++;
                System.out.println("파일 분석 실패: " + file + " - " + e.getMessage());
            }
        }

        long elapsedNanos = System.nanoTime() - startTime;

        return new AnalysisResult(
                input,
                totalWordCounts,
                files.size(),
                successFiles,
                failedFiles,
                skippedFiles,
                elapsedNanos
        );
    }

    private static int collectFiles(Path input, List<Path> files) throws IOException {

        if (Files.isRegularFile(input)) {
            if (!isSupportedFile(input)) {
                throw new IOException("지원하지 않는 파일 형식입니다: " + input);
            }

            files.add(input);
            return 0;
        }

        if (!Files.isDirectory(input)) {
            throw new IOException("읽을 수 있는 파일 또는 폴더가 아닙니다: " + input);
        }

        int skippedFiles = 0;

        try (Stream<Path> stream = Files.list(input)) {

            for (Path child : stream.toList()) {
                if (!Files.isRegularFile(child)) {
                    continue;
                }

                if (isSupportedFile(child)) {
                    files.add(child);
                } else {
                    skippedFiles++;
                }
            }
        }

        if (files.isEmpty()) {
            throw new IOException("폴더에 지원하는 파일이 없습니다: " + input);
        }

        return skippedFiles;
    }

    private static boolean isSupportedFile(Path file) {
        String fileName = file.getFileName().toString().toLowerCase();

        return fileName.endsWith(".txt")
                || fileName.endsWith(".csv")
                || fileName.endsWith(".tsv")
                || fileName.endsWith(".html")
                || fileName.endsWith(".htm");
    }

    private static void mergeCounts(Map<String, Long> total, Map<String, Long> current) {
        current.forEach((word, count) -> total.merge(word, count, Long::sum));
    }
}