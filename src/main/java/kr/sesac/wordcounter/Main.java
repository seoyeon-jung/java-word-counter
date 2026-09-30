package kr.sesac.wordcounter;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        AnalysisResult latestResult = null;
        boolean running = true;

        System.out.println("문서 단어 분석기");

        while (running) {

            printMenu();

            System.out.print("선택 > ");

            String choice = scanner.nextLine().trim();

            switch (choice) {

                case "1":
                    latestResult = startNewAnalysis(scanner);
                    break;

                case "2":
                    showTopWords(scanner, latestResult);
                    break;

                case "3":
                    findWordCount(scanner, latestResult);
                    break;

                case "4":
                    saveResult(latestResult);
                    break;

                case "5":
                    System.out.println("최근 분석 요약은 다음 단계에서 구현합니다.");
                    break;

                case "0":
                    System.out.println("프로그램을 종료합니다.");
                    running = false;
                    break;

                default:
                    System.out.println("올바른 메뉴 번호를 입력하세요.");
                    break;
            }

            System.out.println();
        }

        scanner.close();
    }

    private static AnalysisResult startNewAnalysis(Scanner scanner) {

        while (true) {

            System.out.print("파일 또는 폴더 경로 > ");
            String inputPath = scanner.nextLine().trim();

            try {
                Path input = Path.of(inputPath);
                AnalysisResult result = FileAnalyzer.analyze(input);
                printAnalysisResult(result);
                return result;

            } catch (InvalidPathException e) {
                System.out.println("올바른 경로를 입력하세요.");
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void printMenu() {

        System.out.println();
        System.out.println("1. 새 분석 시작");
        System.out.println("2. 상위 N개 단어 보기");
        System.out.println("3. 특정 단어 횟수 찾기");
        System.out.println("4. 전체 결과 저장");
        System.out.println("5. 최근 분석 요약 보기");
        System.out.println("0. 종료");
    }

    private static void printAnalysisResult(AnalysisResult result) {

        System.out.println();
        System.out.println("분석 완료");

        System.out.println("입력: " + result.getInputPath());

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

        System.out.printf("처리 시간: %.3fms%n", result.getElapsedMillis());
    }

    private static void showTopWords(Scanner scanner, AnalysisResult latestResult) {
        // 아직 분석한 적 없는 경우
        if (latestResult == null) {
            System.out.println("먼저 새 분석을 실행하세요.");
            return;
        }

        // 분석 성공했지만 단어가 하나도 없는 경우
        if (latestResult.getWordCounts().isEmpty()) {
            System.out.println("조회할 단어가 없습니다.");
            return;
        }

        int topN = readTopN(scanner);

        List<Map.Entry<String, Long>> entries = new ArrayList<>(latestResult.getWordCounts().entrySet());

        entries.sort((e1, e2) -> {
            int countCompare = Long.compare(e2.getValue(), e1.getValue());

            if (countCompare != 0) {
                return countCompare;
            }

            return e1.getKey().compareTo(e2.getKey());
        });

        int limit = Math.min(topN, entries.size());

        System.out.println();
        System.out.println("상위 " + topN + "개 단어");

        for (int i = 0; i < limit; i++) {
            Map.Entry<String, Long> entry = entries.get(i);

            System.out.println((i + 1) + ". " + entry.getKey() + " = " + entry.getValue());
        }
    }

    private static int readTopN(Scanner scanner) {
        while (true) {
            System.out.println("몇 개를 볼까요? " + "(Enter: 10) > ");
            String input = scanner.nextLine().trim();

            // 그냥 Enter 누른 경우
            if (input.isEmpty()) {
                return 10;
            }

            try {
                int topN = Integer.parseInt(input);

                if (topN < 1) {
                    System.out.println("1 이상의 숫자를 입력하세요.");
                    continue;
                }

                return topN;
            } catch (NumberFormatException e) {
                System.out.println("1 이상의 숫자를 입력하세요.");
            }
        }
    }

    private static void findWordCount(Scanner scanner, AnalysisResult latestResult) {
        // 아직 분석한 적 없는 경우
        if (latestResult == null) {
            System.out.println("먼저 새 분석을 실행하세요.");
            return;
        }

        while (true) {
            System.out.println("찾을 단어 >");

            String input = scanner.nextLine();
            List<String> words = WordCounter.extractWords(input);

            if (words.size() != 1) {
                System.out.println("단어 하나만 입력하세요.");
                continue;
            }

            String word = words.get(0);
            long count = latestResult.getWordCounts().getOrDefault(word, 0L);

            System.out.println(word + " = " + count);

            return;
        }
    }

    private static void saveResult(AnalysisResult latestResult) {
        // 아직 분석한 적 없는 경우
        if (latestResult == null) {
            System.out.println("먼저 새 분석을 실행하세요.");
            return;
        }

        try {
            Path outputPath = ResultSaver.save(latestResult);
            System.out.println("저장 완료: " + outputPath);
        } catch (IOException e) {
            System.out.println("저장 실패: " + e.getMessage());
        }
    }
}