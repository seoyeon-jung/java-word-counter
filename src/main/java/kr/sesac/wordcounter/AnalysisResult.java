package kr.sesac.wordcounter;

import java.nio.file.Path;
import java.util.Map;

public class AnalysisResult {

    private final Path inputPath;
    private final Map<String, Long> wordCounts;

    private final int attemptedFiles;
    private final int successFiles;
    private final int failedFiles;
    private final int skippedFiles;

    private final long elapsedNanos;

    public AnalysisResult(
            Path inputPath,
            Map<String, Long> wordCounts,
            int attemptedFiles,
            int successFiles,
            int failedFiles,
            int skippedFiles,
            long elapsedNanos
    ) {
        this.inputPath = inputPath;
        this.wordCounts = wordCounts;
        this.attemptedFiles = attemptedFiles;
        this.successFiles = successFiles;
        this.failedFiles = failedFiles;
        this.skippedFiles = skippedFiles;
        this.elapsedNanos = elapsedNanos;
    }

    public Path getInputPath() {
        return inputPath;
    }

    public Map<String, Long> getWordCounts() {
        return wordCounts;
    }

    public int getAttemptedFiles() {
        return attemptedFiles;
    }

    public int getSuccessFiles() {
        return successFiles;
    }

    public int getFailedFiles() {
        return failedFiles;
    }

    public int getSkippedFiles() {
        return skippedFiles;
    }

    public long getElapsedNanos() {
        return elapsedNanos;
    }

    public long getTotalCount() {
        long totalCount = 0L;

        for (long count : wordCounts.values()) {
            totalCount += count;
        }

        return totalCount;
    }

    public int getDistinctCount() {
        return wordCounts.size();
    }

    public double getElapsedMillis() {
        return elapsedNanos / 1_000_000.0;
    }

    public boolean hasSuccessfulFiles() {
        return successFiles > 0;
    }
}
