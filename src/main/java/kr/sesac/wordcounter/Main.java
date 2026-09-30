package kr.sesac.wordcounter;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
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
                    System.out.println("상위 N개 단어 조회는 다음 단계에서 구현합니다.");
                    break;

                case "3":
                    System.out.println("특정 단어 조회는 다음 단계에서 구현합니다.");
                    break;

                case "4":
                    System.out.println("전체 결과 저장은 다음 단계에서 구현합니다.");
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
}