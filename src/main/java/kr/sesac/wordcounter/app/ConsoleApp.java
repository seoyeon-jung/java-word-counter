package kr.sesac.wordcounter.app;

import kr.sesac.wordcounter.analysis.FileAnalyzer;
import kr.sesac.wordcounter.analysis.WordCountSorter;
import kr.sesac.wordcounter.analysis.WordCounter;
import kr.sesac.wordcounter.model.AnalysisConfig;
import kr.sesac.wordcounter.model.AnalysisResult;
import kr.sesac.wordcounter.output.ResultSaver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Stream;

public class ConsoleApp {

    private final Scanner scanner = new Scanner(System.in);
    private AnalysisResult latestResult;

    public void run() {
        System.out.println("문서 단어 분석기");

        boolean running = true;

        while (running) {
            printMenu();

            System.out.print("선택 > ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> startNewAnalysis();
                case "2" -> showTopWords();
                case "3" -> findWordCount();
                case "4" -> saveResult();
                case "5" -> showLatestSummary();
                case "0" -> {
                    System.out.println("프로그램을 종료합니다.");
                    running = false;
                }
                default -> System.out.println("올바른 메뉴 번호를 입력하세요.");
            }

            System.out.println();
        }

        scanner.close();
    }

    private void startNewAnalysis() {
        while (true) {
            System.out.print("파일 또는 폴더 경로 > ");
            String inputPath = scanner.nextLine().trim();

            try {
                Path input = Path.of(inputPath);

                AnalysisConfig config;

                // 분석 대상에 CSV 파일이 있을 때만 CSV 분석 열을 입력받는다.
                if (containsCsv(input)) {
                    String[] csvColumns = readCsvColumns();
                    config = new AnalysisConfig(csvColumns);
                } else {
                    config = AnalysisConfig.defaultConfig();
                }

                AnalysisResult result = FileAnalyzer.analyze(input, config);

                latestResult = result;
                printAnalysisResult(result);
                return;

            } catch (InvalidPathException e) {
                System.out.println("올바른 경로를 입력하세요.");
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * 입력한 경로의 분석 대상에 CSV 파일이 포함되어 있는지 확인한다.
     *
     * 파일을 직접 입력한 경우:
     * - 해당 파일의 확장자가 .csv인지 확인
     *
     * 폴더를 입력한 경우:
     * - 바로 아래의 일반 파일 중 .csv 파일이 있는지 확인
     * - 하위 폴더는 탐색하지 않는다.
     */
    private boolean containsCsv(Path input) {
        if (Files.isRegularFile(input)) {
            return isCsvFile(input);
        }

        if (Files.isDirectory(input)) {
            try (Stream<Path> paths = Files.list(input)) {
                return paths
                    .filter(Files::isRegularFile)
                    .anyMatch(this::isCsvFile);
            } catch (IOException e) {
                return false;
            }
        }

        return false;
    }

    private boolean isCsvFile(Path file) {
        String fileName = file.getFileName().toString().toLowerCase();
        return fileName.endsWith(".csv");
    }

    private String[] readCsvColumns() {
        while (true) {
            System.out.print("CSV 분석 열 (Enter: text, 여러 개는 쉼표로 구분) > ");

            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                return new String[]{"text"};
            }

            String[] columns = Arrays.stream(input.split(","))
                .map(String::trim)
                .filter(column -> !column.isEmpty())
                .toArray(String[]::new);

            if (columns.length > 0) {
                return columns;
            }

            System.out.println("분석할 CSV 열을 하나 이상 입력하세요.");
        }
    }

    private void showTopWords() {
        if (!canQueryResult()) {
            return;
        }

        if (latestResult.getWordCounts().isEmpty()) {
            System.out.println("조회할 단어가 없습니다.");
            return;
        }

        int topN = readTopN();

        List<Map.Entry<String, Long>> entries = WordCountSorter.sortedEntries(latestResult.getWordCounts());

        int limit = Math.min(topN, entries.size());

        System.out.println();
        System.out.println("상위 " + topN + "개 단어");

        for (int i = 0; i < limit; i++) {
            Map.Entry<String, Long> entry = entries.get(i);

            System.out.println((i + 1) + ". " + entry.getKey() + " = " + entry.getValue());
        }
    }

    private int readTopN() {
        while (true) {
            System.out.print("몇 개를 볼까요? (Enter: 10) > ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                return 10;
            }

            try {
                int topN = Integer.parseInt(input);

                if (topN >= 1) {
                    return topN;
                }
            } catch (NumberFormatException ignored) {
            }

            System.out.println("1 이상의 숫자를 입력하세요.");
        }
    }

    private void findWordCount() {
        if (!canQueryResult()) {
            return;
        }

        while (true) {
            System.out.print("찾을 단어 > ");

            List<String> words = WordCounter.extractWords(scanner.nextLine());

            if (words.size() != 1) {
                System.out.println("단어 하나만 입력하세요.");
                continue;
            }

            String word = words.get(0);

            long count =
                latestResult.getWordCounts().getOrDefault(word, 0L);

            System.out.println(word + " = " + count);
            return;
        }
    }

    private void saveResult() {
        if (latestResult == null) {
            System.out.println("먼저 새 분석을 실행하세요.");
            return;
        }

        if (!latestResult.hasSuccessfulFiles()) {
            System.out.println("성공한 분석 결과가 없어 저장할 수 없습니다.");
            return;
        }

        try {
            Path outputPath = ResultSaver.save(latestResult);
            System.out.println("저장 완료: " + outputPath);

        } catch (IOException e) {
            System.out.println("저장 실패: " + e.getMessage());
        }
    }

    private void showLatestSummary() {
        if (latestResult == null) {
            System.out.println("먼저 새 분석을 실행하세요.");
            return;
        }

        System.out.println();
        System.out.println("최근 분석 요약");

        printResultDetails(latestResult);
    }

    private boolean canQueryResult() {
        if (latestResult == null) {
            System.out.println("먼저 새 분석을 실행하세요.");
            return false;
        }

        if (!latestResult.hasSuccessfulFiles()) {
            System.out.println("성공한 분석 결과가 없어 조회할 수 없습니다.");
            return false;
        }

        return true;
    }

    private void printAnalysisResult(AnalysisResult result) {
        System.out.println();
        System.out.println("분석 완료");
        System.out.println("입력: " + result.getInputPath());

        printResultDetails(result);
    }

    private void printResultDetails(AnalysisResult result) {
        System.out.println(
            "파일: 시도 "
                + result.getAttemptedFiles()
                + "개 / 성공 "
                + result.getSuccessFiles()
                + "개 / 실패 "
                + result.getFailedFiles()
                + "개 / 지원하지 않아 건너뜀 "
                + result.getSkippedFiles()
                + "개"
        );

        System.out.println(
            "전체 단어: "
                + result.getTotalCount()
                + "개 / 서로 다른 단어: "
                + result.getDistinctCount()
                + "개"
        );

        System.out.printf(
            "처리 시간: %.3fms%n",
            result.getElapsedMillis()
        );
    }

    private void printMenu() {
        System.out.println();
        System.out.println("1. 새 분석 시작");
        System.out.println("2. 상위 N개 단어 보기");
        System.out.println("3. 특정 단어 횟수 찾기");
        System.out.println("4. 전체 결과 저장");
        System.out.println("5. 최근 분석 요약 보기");
        System.out.println("0. 종료");
    }
}