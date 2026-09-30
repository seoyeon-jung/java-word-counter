package kr.sesac.wordcounter;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class FileParser {

    private static final String[] CSV_COLUMNS = {"text"};
    private static final String[] TSV_COLUMNS = {"document"};
    private static final String HTML_SELECTOR = "#content";

    public static void parse (Path input, Map<String, Long> wordCounts) throws IOException {
        String fileName = input.getFileName().toString().toLowerCase();

        if (fileName.endsWith(".txt")) {
            parseTxt(input, wordCounts);
        } else if (fileName.endsWith(".csv")) {
            parseCsv(input, wordCounts);
        } else if (fileName.endsWith(".tsv")) {
            parseTsv(input, wordCounts);
        } else if (fileName.endsWith(".html") || fileName.endsWith(".htm")) {
            parseHtml(input, wordCounts);
        } else {
            System.out.println("현재 지원하지 않는 파일 형식입니다.");
        }
    }

    // TXT
    private static void parseTxt(Path input, Map<String, Long> wordCounts) throws IOException {

        try (BufferedReader reader = Files.newBufferedReader(input, StandardCharsets.UTF_8)) {

            String line;

            while ((line = reader.readLine()) != null) {
                WordCounter.countWords(line, wordCounts);
            }
        }
    }

    // CSV
    private static void parseCsv(Path input, Map<String, Long> wordCounts) throws IOException {

        try (BufferedReader reader =
                     Files.newBufferedReader(
                             input,
                             StandardCharsets.UTF_8
                     );

             CSVParser parser =
                     CSVFormat.DEFAULT.builder()
                             .setHeader()
                             .setSkipHeaderRecord(true)
                             .get()
                             .parse(reader)) {

            for (CSVRecord record : parser) {
                for (String column : CSV_COLUMNS) {
                    String text = record.get(column);
                    WordCounter.countWords(text, wordCounts);
                }
            }
        }
    }

    // TSV
    private static void parseTsv(
            Path input,
            Map<String, Long> wordCounts
    ) throws IOException {

        try (BufferedReader reader =
                     Files.newBufferedReader(
                             input,
                             StandardCharsets.UTF_8
                     );

             CSVParser parser =
                     CSVFormat.DEFAULT.builder()
                             .setDelimiter('\t')
                             .setHeader()
                             .setSkipHeaderRecord(true)
                             .get()
                             .parse(reader)) {

            for (CSVRecord record : parser) {
                for (String column : TSV_COLUMNS) {
                    String text = record.get(column);
                    WordCounter.countWords(text, wordCounts);
                }
            }
        }
    }

    // HTML
    private static void parseHtml(
            Path input,
            Map<String, Long> wordCounts
    ) throws IOException {

        Document document = Jsoup.parse(input.toFile(), StandardCharsets.UTF_8.name());

        Elements contents = document.select(HTML_SELECTOR);

        if (contents.size() != 1) {
           throw new IOException("HTML 본문 요소를 정확히 하나 찾을 수 없습니다. " + "선택자: " + HTML_SELECTOR
                   + ", 찾은 개수: " + contents.size());
        }

        Element content = contents.getFirst();

        // 본문 분석에서 제외할 요소 제거
        content.select("header, nav, footer, script, style").remove();

        String text = content.text();
        WordCounter.countWords(text, wordCounts);

    }
}
