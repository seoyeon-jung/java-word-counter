package kr.sesac.wordcounter;

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

    public static AnalysisResult analyze(Path input) throws IOException {

        List<Path> files = new ArrayList<>();

        int skippedFiles = 0;

        // 입력 경로 확인
        if (!Files.exists(input)) {
            throw new IOException("경로를 찾을 수 없습니다: " + input);
        }

        if (Files.isRegularFile(input)) {

            // 파일 하나를 직접 입력한 경우
            if (!isSupportedFile(input)) {
                throw new IOException("지원하지 않는 파일 형식입니다: " + input);
            }

            files.add(input);

        } else if (Files.isDirectory(input)) {

            // 폴더 바로 아래의 항목만 확인
            try (Stream<Path> stream = Files.list(input)) {

                List<Path> children = stream.toList();

                for (Path child : children) {

                    // 하위 폴더는 무시
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

        } else {
            throw new IOException("읽을 수 있는 파일 또는 폴더가 아닙니다: " + input);
        }


        Map<String, Long> totalWordCounts = new HashMap<>();

        int attemptedFiles = 0;
        int successFiles = 0;
        int failedFiles = 0;

        long startTime = System.nanoTime();

        for (Path file : files) {

            attemptedFiles++;

            Map<String, Long> fileWordCounts = new HashMap<>();

            try {

                FileParser.parse(file, fileWordCounts);

                // 파일 분석에 완전히 성공한 경우에만 합산
                mergeCounts(totalWordCounts, fileWordCounts);

                successFiles++;

            } catch (IOException | UncheckedIOException | IllegalArgumentException e) {

                failedFiles++;

                System.out.println("파일 분석 실패: " + file + " - " + e.getMessage());
            }
        }

        long endTime = System.nanoTime();

        return new AnalysisResult(
                input,
                totalWordCounts,
                attemptedFiles,
                successFiles,
                failedFiles,
                skippedFiles,
                endTime - startTime
        );
    }

    private static boolean isSupportedFile(Path file) {

        String fileName = file.getFileName().toString().toLowerCase();

        return fileName.endsWith(".txt")
                || fileName.endsWith(".csv")
                || fileName.endsWith(".tsv")
                || fileName.endsWith(".html")
                || fileName.endsWith(".htm");
    }

    private static void mergeCounts(
            Map<String, Long> totalWordCounts,
            Map<String, Long> fileWordCounts
    ) {

        for (Map.Entry<String, Long> entry : fileWordCounts.entrySet()) {

            String word = entry.getKey();
            long count = entry.getValue();

            totalWordCounts.put(word, totalWordCounts.getOrDefault(word, 0L) + count);
        }
    }
}